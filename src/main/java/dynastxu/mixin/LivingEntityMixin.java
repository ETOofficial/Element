package dynastxu.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dynastxu.api.ILivingEntityData;
import dynastxu.elements.*;
import dynastxu.event.ReactionEvent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static dynastxu.network.Network.ELEMENT_REACTION_CHANNEL;
import static dynastxu.network.Network.ELEMENT_SYNC_CHANNEL;

@Mixin(LivingEntity.class) // 关键：只针对有生命的实体！
public abstract class LivingEntityMixin extends Entity implements ILivingEntityData {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    } // 可选接口

    @Shadow
    public abstract float getMaxHealth();

    // 1. 添加你的自定义字段 (仅限有生命实体拥有)
    @Unique
    private List<AttachedElement> attachedElements = new ArrayList<>();
    @Unique
    private float maxElementLevel;
    @Unique
    private float baseAttenuationSpeed;
    @Unique
    private float attenuationSpeed = 1;
    @Unique
    private float elementalMastery = 1;
    @Unique
    private static final int ATTACH_INTERVAL = 2;
    @Unique
    private int attachCooldown = 0;
    @Unique
    private static final int SYNC_INTERVAL = 5; // 同步间隔
    @Unique
    private int syncCooldown = 0;
    @Unique
    private AttachedElement lastAttachedElement = null;
    @Unique
    private float elementAbsorptionAmount = 0;
    @Unique
    private Elements absorptionElement = null;

    @Unique
    public float element$getMaxElementLevel() {
        return maxElementLevel;
    }
    @Unique
    public void element$setMaxElementLevel(float maxElementLevel) {
        this.maxElementLevel = maxElementLevel;
    }
    @Unique
    public float element$getBaseAttenuationSpeed() {
        return baseAttenuationSpeed;
    }
    @Unique
    public void element$setBaseAttenuationSpeed(float baseAttenuationSpeed) {
        this.baseAttenuationSpeed = baseAttenuationSpeed;
    }
    @Unique
    public float element$getAttenuationSpeed() {
        return attenuationSpeed;
    }
    @Unique
    public void element$setAttenuationSpeed(float attenuationSpeed) {
        this.attenuationSpeed = attenuationSpeed;
    }
    @Unique
    public float element$getElementalMastery() {
        return elementalMastery;
    }
    @Unique
    public void element$setElementalMastery(float elementalMastery) {
        this.elementalMastery = elementalMastery;
    }
    @Unique
    public float element$getElementAbsorptionAmount() {
        return elementAbsorptionAmount;
    }
    @Unique
    public void element$setElementAbsorptionAmount(float elementAbsorptionAmount) {
        if (elementAbsorptionAmount < 0) elementAbsorptionAmount = 0;
        this.elementAbsorptionAmount = elementAbsorptionAmount;
    }
    @Unique
    public Elements element$getAbsorptionElement() {
        return absorptionElement;
    }
    @Unique
    public void element$setAbsorptionElement(Elements element) {
        this.absorptionElement = element;
    }

    /**
     * 减少元素吸收量
     * @param amount 减少的元素吸收量
     * @return 实际减少的元素吸收量
     */
    @Unique
    public float reduceElementAbsorptionAmount(float amount) {
        float reducedAmount = Math.min(amount, element$getElementAbsorptionAmount());
        element$setElementAbsorptionAmount(element$getElementAbsorptionAmount() - reducedAmount);
        element$syncDatas();
        return reducedAmount;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    protected void injectInit(CallbackInfo ci) {
        element$setMaxElementLevel(getMaxHealth() / 20 * 3);
        element$setBaseAttenuationSpeed(element$getMaxElementLevel() / 30);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectTick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!(self instanceof SlimeEntity)) {
            // 自然衰减
            Iterator<AttachedElement> iterator = this.attachedElements.iterator();
            while (iterator.hasNext()) {
                AttachedElement element = iterator.next();
                element.attenuation(element$getBaseAttenuationSpeed(), element$getAttenuationSpeed());
                if (element == null || element.getLevel() <= 0) {
                    iterator.remove();
                }
            }
        }

        // 新增：计算浸水百分比
        float waterHeight = (float) self.getFluidHeight(FluidTags.WATER);
        float submergedRatio = Math.min(1.0f, waterHeight / self.getHeight());
        int submergedPercentage = (int)(submergedRatio * 100);

        if (submergedPercentage > 0) {
            element$addAttachedElement(new AttachedElement(Elements.Hydro, submergedPercentage / 20f));
        }

        if (attachCooldown > 0) attachCooldown--;

        // 同步数据
        if (!self.getWorld().isClient()) {
            if (syncCooldown <= 0) {
                element$syncDatas();
                syncCooldown = SYNC_INTERVAL;
            } else {
                syncCooldown--;
            }
        }
    }

    // 持久化数据 (如果需要保存)
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void injectWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList elementList = new NbtList();

        for (AttachedElement element : this.attachedElements) {
            elementList.add(element.serialize()); // 序列化每个元素
        }
        nbt.put("attached_elements", elementList); // 保存列表

        nbt.putFloat("max_element_level", maxElementLevel);
        nbt.putFloat("attenuation_speed", attenuationSpeed);
        nbt.putFloat("elemental_mastery", elementalMastery);
        if (lastAttachedElement != null) {
            nbt.put("last_attached_element", lastAttachedElement.serialize());
        } else {
            nbt.put("last_attached_element", new NbtCompound()); // 存入空值占位符
        }
        nbt.putFloat("element_absorption_amount", elementAbsorptionAmount);
        if (absorptionElement != null) {
            nbt.putString("absorption_element", absorptionElement.name());
        } else {
            nbt.putString("absorption_element", ""); // 存入空值占位符
        }
        nbt.putFloat("base_attenuation_speed", baseAttenuationSpeed);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void injectReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.attachedElements.clear(); // 清空现有列表

        if (nbt.contains("attached_elements", NbtElement.LIST_TYPE)) {
            NbtList elementList = nbt.getList("attached_elements", NbtElement.COMPOUND_TYPE);

            for (int i = 0; i < elementList.size(); i++) {
                NbtCompound elementNbt = elementList.getCompound(i);
                AttachedElement element = AttachedElement.deserialize(elementNbt); // 反序列化
                this.attachedElements.add(element);
            }
        }

        this.maxElementLevel = nbt.getFloat("max_element_level");
        this.attenuationSpeed = nbt.getFloat("attenuation_speed");
        this.elementalMastery = nbt.getFloat("elemental_mastery");
        NbtCompound lastElementNbt = nbt.getCompound("last_attached_element");
        this.lastAttachedElement = !lastElementNbt.isEmpty() ?
                AttachedElement.deserialize(lastElementNbt) : null;
        this.elementAbsorptionAmount = nbt.getFloat("element_absorption_amount");
        String elementName = nbt.getString("absorption_element");
        try {
            this.absorptionElement = (!elementName.isEmpty() && !elementName.startsWith("{")) ?
                    Elements.valueOf(elementName) : null;
        } catch (IllegalArgumentException e) {
            this.absorptionElement = null; // 无效元素名时重置为 null
        }
        this.baseAttenuationSpeed = nbt.getFloat("base_attenuation_speed");
    }

//    @Inject(method = "getAbsorptionAmount", at = @At("RETURN"), cancellable = true)
//    private void injectGetAbsorptionAmount(CallbackInfoReturnable<Float> cir) {
//        cir.setReturnValue(cir.getReturnValue() + element$getElementAbsorptionAmount());
//    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyDamageAmount(float amount, @Local(ordinal = 0, argsOnly = true) DamageSource source) {
        if (source.isIn(DamageTypeTags.IS_FIRE)) {
            ReactionResult reactionResult = element$addAttachedElement(new AttachedElement(Elements.Pyro, amount / getMaxHealth() * 2));
            if (reactionResult != null) {
                if (reactionResult.reaction() == Reactions.Vaporize) {
                    amount *= (1 +  reactionResult.reactionValue()) * 1.5f;
                } else if (reactionResult.reaction() == Reactions.Melt) {
                    amount *= (1 + reactionResult.reactionValue()) * 2;
                }
            }
        }
        return amount; // 返回原始值或修改后的值
    }

    @ModifyVariable(method = "modifyAppliedDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyModifyApplyDamageAmount(float amount, @Local(ordinal = 0, argsOnly = true) DamageSource source) {
        if (!this.isInvulnerableTo(source)) {
            if (lastAttachedElement != null && lastAttachedElement.getElement() == absorptionElement) amount /= 2.5f; // 同元素伤害减少60%
            amount -= reduceElementAbsorptionAmount(amount);
        }
        if (amount < 0) amount = 0;
        return amount;
    }

    @Unique
    public void element$syncDatas() {
        if (!this.getWorld().isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(this.getId());

            NbtCompound data = new NbtCompound();
            NbtList elementsList = new NbtList();
            for (AttachedElement element : this.attachedElements) {
                elementsList.add(element.serialize());
            }
            data.put("elements", elementsList); // 将列表放入 compound 中
            buf.writeNbt(data);

            buf.writeFloat(elementAbsorptionAmount);
            if (absorptionElement != null) {
                buf.writeString(absorptionElement.name());
            } else {
                buf.writeString(""); // 存入空值占位符
            }

            // 发送给所有跟踪此实体的玩家
            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) this.getWorld(), this.getBlockPos())) {
                ServerPlayNetworking.send(player, ELEMENT_SYNC_CHANNEL, buf);
            }
        }
    }

    @Unique
    public ReactionResult element$addAttachedElement(AttachedElement element) {
        if (attachCooldown > 0) return null;
        attachCooldown = ATTACH_INTERVAL;
        lastAttachedElement = element;
        // 元素已经存在
        for (AttachedElement existingElement : this.attachedElements) {
            if (existingElement.getElement() == element.getElement()) {
                existingElement.setLevel(existingElement.getLevel() + element.getLevel());
                if (existingElement.getLevel() > element$getMaxElementLevel()) {
                    existingElement.setLevel(element$getMaxElementLevel());
                }
                return null;
            }
        }

        // 元素不存在
        this.attachedElements.add(element);
        ReactionResult reactionResult = element$reactionElements(element, this.attachedElements);

        // 风元素不可附着
        this.attachedElements.removeIf(e -> e.getElement() == Elements.Anemo);

        // 同步数据
        if (!this.getWorld().isClient()) {
            this.element$syncDatas();
        }

        return reactionResult;
    }

    @Unique
    public List<AttachedElement> element$getAttachedElements() {
        return this.attachedElements;
    }

    @Unique
    public void element$setAttachedElements(List<AttachedElement> elements) {
        this.attachedElements = elements;
    }

    @Unique
    public ReactionResult element$reactionElements(AttachedElement element, List<AttachedElement> elements) {
        List<AttachedElement> toRemove = new ArrayList<>(); // 创建待删除列表
        ReactionResult reactionResult = null;
        Elements swirlElement = null;
        switch (element.getElement()) {
            case Pyro -> {
                for (AttachedElement attachedElement : elements) {
                    switch (attachedElement.getElement()) {
                        case Hydro -> // 火 -> 水
                                reactionResult = ReactionHandler.vaporize(attachedElement, element);
                        case Cryo -> // 火 -> 冰
                                reactionResult = ReactionHandler.melt(element, attachedElement);
                        case Electro -> // 火 -> 雷
                                reactionResult = ReactionHandler.overload(element, attachedElement);
                    }
                    // 清理空元素
                    if (attachedElement.getLevel() <= 0) toRemove.add(attachedElement);
                }
            }
            case Hydro -> {
                for (AttachedElement attachedElement : elements) {
                    switch (attachedElement.getElement()) {
                        case Pyro -> // 水 -> 火
                                reactionResult = ReactionHandler.vaporize(element, attachedElement);
                    }
                    // 清理空元素
                    if (attachedElement.getLevel() <= 0) toRemove.add(attachedElement);
                }
            }
            case Cryo -> {
                for (AttachedElement attachedElement : elements) {
                    switch (attachedElement.getElement()) {
                        case Pyro -> // 冰 -> 火
                                reactionResult = ReactionHandler.melt(attachedElement, element);
                        case Electro -> // 冰 -> 雷
                                reactionResult = ReactionHandler.superconduct(attachedElement, element);
                    }
                    // 清理空元素
                    if (attachedElement.getLevel() <= 0) toRemove.add(attachedElement);
                }
            }
            case Electro -> {
                for (AttachedElement attachedElement : elements) {
                    switch (attachedElement.getElement()) {
                        case Pyro -> // 雷 -> 火
                                reactionResult = ReactionHandler.overload(attachedElement, element);
                        case Cryo -> // 雷 -> 冰
                                reactionResult = ReactionHandler.superconduct(element, attachedElement);
                    }
                    // 清理空元素
                    if (attachedElement.getLevel() <= 0) toRemove.add(attachedElement);
                }
            }
            case Anemo -> {
                for (AttachedElement attachedElement : elements) {
                    switch (attachedElement.getElement()) {
                        case Pyro -> {// 风 -> 火
                            reactionResult = ReactionHandler.swirl(element, attachedElement);
                            swirlElement = attachedElement.getElement();
                        }
                        case Hydro -> { // 风 -> 水
                            reactionResult = ReactionHandler.swirl(element, attachedElement);
                            swirlElement = attachedElement.getElement();
                        }
                        case Cryo -> { // 风 -> 冰
                            reactionResult = ReactionHandler.swirl(element, attachedElement);
                            swirlElement = attachedElement.getElement();
                        }
                        case Electro -> {// 风 -> 雷
                            reactionResult = ReactionHandler.swirl(element, attachedElement);
                            swirlElement = attachedElement.getElement();
                        }
                    }
                }
            }
        }

        // 清理空元素
        if (element.getLevel() <= 0) toRemove.add(element);

        elements.removeAll(toRemove);

        // 立即同步最新状态
        this.element$syncDatas();

        // 触发反应
        if (reactionResult != null && reactionResult.reaction() != null) {
            element$triggerReaction(reactionResult, swirlElement);
        }

        return reactionResult;
    }

    @Unique
    public void element$triggerReaction(ReactionResult reactionResult, Elements swirlElement) {
        if (!this.getWorld().isClient())
            ReactionEvent.event((LivingEntity) (Object) this, reactionResult, swirlElement);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(this.getId());
        buf.writeEnumConstant(reactionResult.reaction());
        buf.writeFloat(reactionResult.reactionValue());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) this.getWorld(),
                this.getBlockPos())) {
            ServerPlayNetworking.send(player, ELEMENT_REACTION_CHANNEL, buf);
        }
    }
}
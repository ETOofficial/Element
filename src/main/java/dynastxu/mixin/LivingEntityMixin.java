package dynastxu.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dynastxu.api.ILivingEntityData;
import dynastxu.elements.*;
import dynastxu.event.ReactionEvent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
public abstract class LivingEntityMixin implements ILivingEntityData { // 可选接口

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
    private float attenuationSpeed;
    @Unique
    private float elementalMastery;
    @Unique
    private static final int SYNC_INTERVAL = 5; // 同步间隔
    @Unique
    private int syncCooldown = 0;

    @Inject(method = "<init>", at = @At("TAIL"))
    protected void init(CallbackInfo ci) {
        maxElementLevel = getMaxHealth() / 20 * 3;
        baseAttenuationSpeed = maxElementLevel / 30;
        attenuationSpeed = 1;
        elementalMastery = 1;
    }

    // 4. 【重要】处理生命周期事件 (示例：在tick中更新)
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        // 每刻更新你的自定义逻
        Iterator<AttachedElement> iterator = this.attachedElements.iterator();
        while (iterator.hasNext()) {
            AttachedElement element = iterator.next();
            element.attenuation(baseAttenuationSpeed, attenuationSpeed);
            if (element == null || element.getLevel() <= 0) {
                iterator.remove();
            }
        }

        // 新增：计算浸水百分比
        float waterHeight = (float) self.getFluidHeight(FluidTags.WATER);
        float submergedRatio = Math.min(1.0f, waterHeight / self.getHeight());
        int submergedPercentage = (int)(submergedRatio * 100);

        if (submergedPercentage > 0) {
            element$addAttachedElement(new AttachedElement(Elements.Hydro, submergedPercentage / 20f));
        }
        // 同步数据
        if (!self.getWorld().isClient()) {
            if (syncCooldown <= 0) {
                element$syncAttachedElements();
                syncCooldown = SYNC_INTERVAL;
            } else {
                syncCooldown--;
            }
        }
    }

    // 5. 【重要】持久化数据 (如果需要保存)
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList elementList = new NbtList();

        for (AttachedElement element : this.attachedElements) {
            elementList.add(element.serialize()); // 序列化每个元素
        }

        nbt.put("attached_elements", elementList); // 保存列表
        nbt.putFloat("attenuation_speed", attenuationSpeed);
        nbt.putFloat("elemental_mastery", elementalMastery);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.attachedElements.clear(); // 清空现有列表

        if (nbt.contains("attached_elements", NbtElement.LIST_TYPE)) {
            NbtList elementList = nbt.getList("attached_elements", NbtElement.COMPOUND_TYPE);

            for (int i = 0; i < elementList.size(); i++) {
                NbtCompound elementNbt = elementList.getCompound(i);
                AttachedElement element = AttachedElement.deserialize(elementNbt); // 反序列化
                this.attachedElements.add(element);
            }
        }
        this.attenuationSpeed = nbt.getFloat("attenuation_speed");
        this.elementalMastery = nbt.getFloat("elemental_mastery");
    }

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

    @Override
    public void element$syncAttachedElements() {
        if (!((LivingEntity) (Object) this).getWorld().isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(((LivingEntity) (Object) this).getId());

            NbtCompound data = new NbtCompound();
            NbtList list = new NbtList();
            for (AttachedElement element : this.attachedElements) {
                list.add(element.serialize());
            }
            data.put("elements", list); // 将列表放入 compound 中
            buf.writeNbt(data); // 正确调用 writeNbt(NbtCompound)

            // 发送给所有跟踪此实体的玩家
            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) ((LivingEntity) (Object) this).getWorld(), ((LivingEntity) (Object) this).getBlockPos())) {
                ServerPlayNetworking.send(player, ELEMENT_SYNC_CHANNEL, buf);
            }
        }
    }

    @Override
    public ReactionResult element$addAttachedElement(AttachedElement element) {
        // 元素已经存在
        for (AttachedElement existingElement : this.attachedElements) {
            if (existingElement.getElement() == element.getElement()) {
                existingElement.setLevel(existingElement.getLevel() + element.getLevel());
                if (existingElement.getLevel() > maxElementLevel) {
                    existingElement.setLevel(maxElementLevel);
                }
                return null;
            }
        }

        // 元素不存在
        this.attachedElements.add(element);
        ReactionResult reactionResult = element$reactionElements(element, this.attachedElements);

        if (!((LivingEntity) (Object) this).getWorld().isClient()) {
            this.element$syncAttachedElements();
        }

        return reactionResult;
    }

    @Override
    public List<AttachedElement> element$getAttachedElements() {
        return this.attachedElements;
    }

    @Override
    public void element$setAttachedElements(List<AttachedElement> elements) {
        this.attachedElements = elements;
    }

    @Override
    public ReactionResult element$reactionElements(AttachedElement element, List<AttachedElement> elements) {
        List<AttachedElement> toRemove = new ArrayList<>(); // 创建待删除列表
        ReactionResult reactionResult = null;
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
        }

        // 清理空元素
        if (element.getLevel() <= 0) toRemove.add(element);

        elements.removeAll(toRemove);

        // 立即同步最新状态
        this.element$syncAttachedElements();

        // 触发反应
        if (reactionResult != null && reactionResult.reaction() != null) {
            element$triggerReaction(reactionResult);
        }

        return reactionResult;
    }

    @Override
    public void element$triggerReaction(ReactionResult reactionResult) {
        if (!((LivingEntity) (Object) this).getWorld().isClient())
            ReactionEvent.event((LivingEntity) (Object) this, reactionResult);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(((LivingEntity) (Object) this).getId());
        buf.writeEnumConstant(reactionResult.reaction());
        buf.writeFloat(reactionResult.reactionValue());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) ((LivingEntity) (Object) this).getWorld(),
                ((LivingEntity) (Object) this).getBlockPos())) {
            ServerPlayNetworking.send(player, ELEMENT_REACTION_CHANNEL, buf);
        }
    }
}
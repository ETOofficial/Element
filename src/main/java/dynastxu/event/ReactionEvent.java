package dynastxu.event;

import dynastxu.api.ILivingEntityData;
import dynastxu.damage_type.ElementDamageType;
import dynastxu.elements.AttachedElement;
import dynastxu.elements.Elements;
import dynastxu.elements.ReactionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

import static dynastxu.Element.MAX_DIFFUSION_DISTANCE;

public class ReactionEvent {
    public static void overloadEvent(Entity entity, float power) {
        // 1. 安全检查
        if (entity == null || entity.getWorld().isClient()) return;

        World world = entity.getWorld();
        // 获取立方体内的所有实体
        List<Entity> targets = getOtherEntities(entity, power, world);

        // 2. 创建伤害源
        DamageSource damageSource = ElementDamageType.createDamageSource(world);


        for (Entity target : targets) {
            if (target instanceof LivingEntity livingEntity) {
                float distance = (float) livingEntity.getPos().distanceTo(entity.getPos());
                float damage = 1 - distance / (power / 2); // 线性衰减
                livingEntity.damage(damageSource, damage);
            }
        }
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.damage(damageSource, power);
        }

        // 4. 添加爆炸效果（可选，参数：位置坐标、威力、是否破坏方块）
        world.createExplosion(
                entity,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                power,  // 爆炸威力（视觉效果）
                World.ExplosionSourceType.NONE
        );
    }

    public static void superconductEvent(Entity entity, float power){
        if (entity == null || entity.getWorld().isClient()) return;

        diffusionElementAndDamage(entity, power, Elements.Cryo);
    }

    public static void swirlEvent(Entity entity, float power, Elements element) {
        if (entity == null || entity.getWorld().isClient()) return;
        if (element != Elements.Hydro && element != Elements.Pyro && element != Elements.Cryo && element != Elements.Electro) return;

        diffusionElementAndDamage(entity, power, power + 3, element);
    }

    private static void diffusionElementAndDamage(Entity entity, float power, Elements element){
        diffusionElementAndDamage(entity, power, power, element);
    }

    private static void diffusionElementAndDamage(Entity entity, float power, float diameter, Elements element){
        World world = entity.getWorld();
        DamageSource damageSource = ElementDamageType.createDamageSource(world);
        // 获取立方体内的所有实体
        List<Entity> targets = getOtherEntities(entity, diameter, world);

        for (Entity target : targets) {
            if (target instanceof LivingEntity livingEntity && livingEntity instanceof ILivingEntityData) {
                float distance = (float) livingEntity.getPos().distanceTo(entity.getPos());
                float attenuatedPower = 1 - distance / (power / 2); // 线性衰减
                livingEntity.damage(damageSource, attenuatedPower);
                ((ILivingEntityData) livingEntity).element$addAttachedElement(new AttachedElement(element, attenuatedPower));
            }
        }
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.damage(damageSource, power);
        }
    }

    private static List<Entity> getOtherEntities(Entity entity, float diameter, World world){
        if (diameter > MAX_DIFFUSION_DISTANCE) diameter = MAX_DIFFUSION_DISTANCE;
        return world.getOtherEntities(
                entity,
                new Box(entity.getX() - diameter / 2, entity.getY() - diameter / 2, entity.getZ() - diameter / 2,
                        entity.getX() + diameter / 2, entity.getY() + diameter / 2, entity.getZ() + diameter / 2)
        );
    }

    public static void event(Entity entity, ReactionResult reactionResult, Elements element){
        switch (reactionResult.reaction()){
            case Overload:
                overloadEvent(entity, reactionResult.reactionValue());
                break;
            case Superconduct:
                superconductEvent(entity, reactionResult.reactionValue());
                break;
            case Swirl:
                if (element == null) throw new IllegalArgumentException("Element cannot be null because the reaction need an element!");
                swirlEvent(entity, reactionResult.reactionValue(), element);
                break;
        }
    }
}

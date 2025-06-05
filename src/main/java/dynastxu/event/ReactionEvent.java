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

public class ReactionEvent {


    public static void overloadEvent(Entity entity, float power) {
        // 1. 安全检查
        if (entity == null || entity.getWorld().isClient()) return;

        World world = entity.getWorld();
        // 获取立方体内的所有实体
        List<Entity> targets = world.getOtherEntities(
                entity,
                new Box(entity.getX() - power / 2, entity.getY() - power / 2, entity.getZ() - power / 2,
                        entity.getX() + power / 2, entity.getY() + power / 2, entity.getZ() + power / 2)
        );

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

        World world = entity.getWorld();

        DamageSource damageSource = ElementDamageType.createDamageSource(world);

        // 获取立方体内的所有实体
        float diameter = power + 1;
        List<Entity> targets = world.getOtherEntities(
                entity,
                new Box(entity.getX() - diameter / 2, entity.getY() - diameter / 2, entity.getZ() - diameter / 2,
                        entity.getX() + diameter / 2, entity.getY() + diameter / 2, entity.getZ() + diameter / 2)
        );

        for (Entity target : targets) {
            if (target instanceof LivingEntity livingEntity && livingEntity instanceof ILivingEntityData) {
                float distance = (float) livingEntity.getPos().distanceTo(entity.getPos());
                power = 1 - distance / (power / 2); // 线性衰减
                livingEntity.damage(damageSource, power);
                ((ILivingEntityData) livingEntity).element$addAttachedElement(new AttachedElement(Elements.Cryo, power));
            }
        }
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.damage(damageSource, power);
        }
    }

    public static void event(Entity entity, ReactionResult reactionResult){
        switch (reactionResult.reaction()){
            case Overload:
                overloadEvent(entity, reactionResult.reactionValue());
                break;
            case Superconduct:
                superconductEvent(entity, reactionResult.reactionValue());
                break;
        }
    }
}

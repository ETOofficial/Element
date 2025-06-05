package dynastxu.damage_type;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static dynastxu.Element.MOD_ID;

public class ElementDamageType {
    public static final RegistryKey<DamageType> ELEMENT_DAMAGE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,
            Identifier.of(MOD_ID, "element_damage")
    );

    public static DamageSource createDamageSource(World world, Entity attacker) {
        // 获取伤害类型注册表
        Registry<DamageType> damageTypeRegistry = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE);

        // 获取 RegistryEntry (关键修复)
        RegistryEntry<DamageType> damageTypeEntry = damageTypeRegistry.getEntry(ELEMENT_DAMAGE)
                .orElseThrow(() -> new IllegalStateException("Element damage type not found!"));

        // 使用正确的构造函数 (接受 RegistryEntry<DamageType>)
        return new DamageSource(damageTypeEntry, attacker);
    }

    public static DamageSource createDamageSource(World world){
        return createDamageSource(world, null);
    }
}

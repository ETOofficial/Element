package dynastxu.potions;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static dynastxu.Element.MOD_ID;
import static dynastxu.effects.ModEffects.*;

public class ModPotions {
    public static final Potion INSTANT_HYDRO_POTION = registryHelper("instant_hydro", INSTANT_HYDRO_EFFECT.value());
    public static final Potion INSTANT_PYRO_POTION = registryHelper("instant_pyro", INSTANT_PYRO_EFFECT.value());
    public static final Potion INSTANT_ANEMO_POTION = registryHelper("instant_anemo", INSTANT_ANEMO_EFFECT.value());
    public static final Potion INSTANT_ELECTRO_POTION = registryHelper("instant_electro", INSTANT_ELECTRO_EFFECT.value());
    public static final Potion INSTANT_CRYO_POTION = registryHelper("instant_cryo", INSTANT_CRYO_EFFECT.value());
    public static final Potion INSTANT_DENDRO_POTION = registryHelper("instant_dendro", INSTANT_DENDRO_EFFECT.value());
    public static final Potion INSTANT_GEO_POTION = registryHelper("instant_geo", INSTANT_GEO_EFFECT.value());

    public static final Potion INSTANT_HYDRO_POTION_II = registryHelper("instant_hydro_ii", INSTANT_HYDRO_EFFECT.value(), 1);
    public static final Potion INSTANT_PYRO_POTION_II = registryHelper("instant_pyro_ii", INSTANT_PYRO_EFFECT.value(), 1);
    public static final Potion INSTANT_ANEMO_POTION_II = registryHelper("instant_anemo_ii", INSTANT_ANEMO_EFFECT.value(), 1);
    public static final Potion INSTANT_ELECTRO_POTION_II = registryHelper("instant_electro_ii", INSTANT_ELECTRO_EFFECT.value(), 1);
    public static final Potion INSTANT_CRYO_POTION_II = registryHelper("instant_cryo_ii", INSTANT_CRYO_EFFECT.value(), 1);
    public static final Potion INSTANT_DENDRO_POTION_II = registryHelper("instant_dendro_ii", INSTANT_DENDRO_EFFECT.value(), 1);
    public static final Potion INSTANT_GEO_POTION_II = registryHelper("instant_geo_ii", INSTANT_GEO_EFFECT.value(), 1);

    public static void register() {
    }

    private static Potion registryHelper(String baseName, StatusEffect effect, int amplifier) {
        return Registry.register(
                Registries.POTION,
                Identifier.of(MOD_ID, baseName),
                new Potion(baseName,
                        new StatusEffectInstance(
                                effect,
                                1,
                                amplifier)));
    }

    private static Potion registryHelper(String baseName, StatusEffect effect) {
        return registryHelper(baseName, effect, 0);
    }
}

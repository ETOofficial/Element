package dynastxu.effects;

import dynastxu.elements.Elements;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import static dynastxu.Element.MOD_ID;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> INSTANT_HYDRO_EFFECT = instantElementEffectRegistryHelper("instant_hydro", Elements.Hydro);
    public static final RegistryEntry<StatusEffect> INSTANT_PYRO_EFFECT = instantElementEffectRegistryHelper("instant_pyro", Elements.Pyro);
    public static final RegistryEntry<StatusEffect> INSTANT_ANEMO_EFFECT = instantElementEffectRegistryHelper("instant_anemo", Elements.Anemo);
    public static final RegistryEntry<StatusEffect> INSTANT_CRYO_EFFECT = instantElementEffectRegistryHelper("instant_cryo", Elements.Cryo);
    public static final RegistryEntry<StatusEffect> INSTANT_ELECTRO_EFFECT = instantElementEffectRegistryHelper("instant_electro", Elements.Electro);
    public static final RegistryEntry<StatusEffect> INSTANT_DENDRO_EFFECT = instantElementEffectRegistryHelper("instant_dendro", Elements.Dendro);
    public static final RegistryEntry<StatusEffect> INSTANT_GEO_EFFECT = instantElementEffectRegistryHelper("instant_geo", Elements.Geo);

    public static final RegistryEntry<StatusEffect> HYDRO_SHIELDS_EFFECT = elementShieldsEffectRegistryHelper("hydro_shields", Elements.Hydro);
    public static final RegistryEntry<StatusEffect> PYRO_SHIELDS_EFFECT = elementShieldsEffectRegistryHelper("pyro_shields", Elements.Pyro);
    public static final RegistryEntry<StatusEffect> CRYO_SHIELDS_EFFECT = elementShieldsEffectRegistryHelper("cryo_shields", Elements.Cryo);
    public static final RegistryEntry<StatusEffect> ELECTRO_SHIELDS_EFFECT = elementShieldsEffectRegistryHelper("electro_shields", Elements.Electro);

    public static void register() {}

    private static RegistryEntry<StatusEffect> instantElementEffectRegistryHelper(String path, Elements element) {
        return Registry.registerReference(
                Registries.STATUS_EFFECT,
                Identifier.of(MOD_ID, path),
                new InstantElementEffect(element)
        );
    }

    private static RegistryEntry<StatusEffect> elementShieldsEffectRegistryHelper(String path, Elements element) {
        return Registry.registerReference(
                Registries.STATUS_EFFECT,
                Identifier.of(MOD_ID, path),
                new ElementShieldsEffect(element)
        );
    }
}

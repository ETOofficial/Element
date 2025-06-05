package dynastxu.effects;

import dynastxu.elements.Elements;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import static dynastxu.Element.MOD_ID;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> INSTANT_HYDRO_EFFECT = registryHelper("instant_hydro", Elements.Hydro);
    public static final RegistryEntry<StatusEffect> INSTANT_PYRO_EFFECT = registryHelper("instant_pyro", Elements.Pyro);
    public static final RegistryEntry<StatusEffect> INSTANT_ANEMO_EFFECT = registryHelper("instant_anemo", Elements.Anemo);
    public static final RegistryEntry<StatusEffect> INSTANT_CRYO_EFFECT = registryHelper("instant_cryo", Elements.Cryo);
    public static final RegistryEntry<StatusEffect> INSTANT_ELECTRO_EFFECT = registryHelper("instant_electro", Elements.Electro);
    public static final RegistryEntry<StatusEffect> INSTANT_DENDRO_EFFECT = registryHelper("instant_dendro", Elements.Dendro);
    public static final RegistryEntry<StatusEffect> INSTANT_GEO_EFFECT = registryHelper("instant_geo", Elements.Geo);


    public static void register() {
    }

    private static RegistryEntry<StatusEffect> registryHelper(String path, Elements element) {
        return Registry.registerReference(
                Registries.STATUS_EFFECT,
                Identifier.of(MOD_ID, path),
                new InstantElementEffect(element)
        );
    }
}

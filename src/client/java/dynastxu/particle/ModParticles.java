package dynastxu.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static dynastxu.Element.MOD_ID;

// ModParticles.java
public class ModParticles {
    public static final ParticleType<?> TEXT_PARTICLE = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "empty_particle"), TEXT_PARTICLE);
    }
}
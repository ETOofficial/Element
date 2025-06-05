package dynastxu.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.ParticleType;

public class ModParticleFactories {
    public static void register() {
        // 注册粒子工厂
        ParticleFactoryRegistry.getInstance().register(
                (ParticleType<TextParticleEffect>) ModParticles.TEXT_PARTICLE,
                TextParticleFactory::new  // 使用方法引用简化
        );
    }
}

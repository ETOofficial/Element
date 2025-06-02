package dynastxu.particle;

import dynastxu.render.TextParticlesTextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class TextParticleFactory implements ParticleFactory<TextParticleEffect> {
    private final SpriteProvider spriteProvider;
    private final TextRenderer textRenderer;


    public TextParticleFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public Particle createParticle(TextParticleEffect parameters, ClientWorld world,
                                   double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        var particle = new TextParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider, parameters.getText(), parameters.getTextColor(), parameters.getTextScale(), textRenderer);
        particle.setSprite(this.spriteProvider);
        // 添加到渲染器
        TextParticlesTextRenderer.addParticle(particle);
        return particle;
    }
}
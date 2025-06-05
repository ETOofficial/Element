package dynastxu.event;

import dynastxu.particle.TextParticleEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static dynastxu.elements.ReactionsInfo.*;

public class ReactionEventClient {
    public static void vaporateEvent(MinecraftClient client, Entity entity) {
        if (client.world == null || client.player == null) return;

        Vec3d pos = entity.getPos();

        playSoundOfExtinguishFire(client, pos);

        addTextParticles(Text.translatable("element.vaporize").getString(), VAPORIZE_COLOR, client, pos);

    }

    public static void meltEvent(MinecraftClient client, Entity entity) {
        if (client.world == null || client.player == null) return;

        Vec3d pos = entity.getPos();

        playSoundOfExtinguishFire(client, pos);

        addTextParticles(Text.translatable("element.melt").getString(), MELT_COLOR, client, pos);
    }

    public static void overloadEvent(MinecraftClient client, Entity entity) {
        if (client.world == null || client.player == null) return;

        Vec3d pos = entity.getPos();

        addTextParticles(Text.translatable("element.overload").getString(), OVERLOAD_COLOR, client, pos);
    }

    public static void superconductEvent(MinecraftClient client, Entity entity) {
        if (client.world == null || client.player == null) return;

        Vec3d pos = entity.getPos();

        addTextParticles(Text.translatable("element.superconduct").getString(), SUPERCONDUCT_COLOR, client, pos);
    }

    private static void addTextParticles(String text, int color, MinecraftClient client, Vec3d pos){
        if (client.world == null || client.player == null) return;
        // 新增粒子效果
        for (int i = 0; i < 5; i++) {
            double offsetX = client.world.random.nextGaussian() * 0.5;
            double offsetY = client.world.random.nextDouble();
            double offsetZ = client.world.random.nextGaussian() * 0.5;

            client.world.addParticle(
                    new TextParticleEffect(text, color, 2),
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    0, 0.1, 0
            );
        }
    }

    private static void playSoundOfExtinguishFire(MinecraftClient client, Vec3d pos) {
        if (client.world == null || client.player == null) return;

        // 播放火焰熄灭音效（客户端执行）
        client.getSoundManager().play(
                new PositionedSoundInstance(
                        SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                        SoundCategory.AMBIENT,
                        1.0f,
                        1.0f,
                        client.world.getRandom(),
                        pos.x,
                        pos.y,
                        pos.z
                )
        );
    }
}

package dynastxu.render;

import dynastxu.particle.TextParticle;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class TextParticlesTextRenderer {
    private static final List<TextParticle> ACTIVE_PARTICLES = new ArrayList<>();

    public static void addParticle(TextParticle particle) {
        ACTIVE_PARTICLES.add(particle);
    }

    public static void render(WorldRenderContext context) {
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        VertexConsumerProvider vertexConsumers = context.consumers();
        float tickDelta = context.tickDelta();

        // 保存当前矩阵状态
        matrices.push();

        // 获取相机位置
        Vec3d cameraPos = camera.getPos();

        for (TextParticle particle : ACTIVE_PARTICLES) {
            renderParticle(particle, matrices, camera, vertexConsumers, tickDelta, cameraPos);
        }

        matrices.pop();
    }

    private static void renderParticle(TextParticle particle, MatrixStack matrices, Camera camera,
                                       VertexConsumerProvider vertexConsumers, float tickDelta, Vec3d cameraPos) {
        // 计算粒子的插值位置
        double x = MathHelper.lerp(tickDelta, particle.getPrevPosX(), particle.getX());
        double y = MathHelper.lerp(tickDelta, particle.getPrevPosY(), particle.getY());
        double z = MathHelper.lerp(tickDelta, particle.getPrevPosZ(), particle.getZ());

        // 计算相对于相机的位置
        double relX = x - cameraPos.x;
        double relY = y - cameraPos.y;
        double relZ = z - cameraPos.z;

        // 计算粒子到相机的距离
        double distance = Math.sqrt(relX * relX + relY * relY + relZ * relZ);

        // 如果粒子太远，跳过渲染
        if (distance > 64.0) {
            return;
        }

        // 获取文字内容
        String text = particle.getDisplayText();
        TextRenderer textRenderer = particle.textRenderer;

        // 计算文字尺寸
        float textWidth = textRenderer.getWidth(text);
        float textHeight = textRenderer.fontHeight;

        // 获取颜色和透明度
        int colorValue = particle.getTextColor();
        float red = ((colorValue >> 16) & 0xFF) / 255f;
        float green = ((colorValue >> 8) & 0xFF) / 255f;
        float blue = (colorValue & 0xFF) / 255f;
        float baseAlpha = ((colorValue >> 24) & 0xFF) / 255f;
        if (baseAlpha <= 0) baseAlpha = 1.0f; // 默认完全不透明
        float particleAlpha = 255;
        float alpha = baseAlpha * particleAlpha;

        // 如果完全透明，跳过渲染
        if (alpha <= 0.01f) {
            return;
        }

        // 计算最终颜色
        int argb = ColorHelper.Argb.getArgb((int)(alpha * 255),
                (int)(red * 255),
                (int)(green * 255),
                (int)(blue * 255));

        // 保存当前矩阵状态
        matrices.push();

        // 移动到粒子位置
        matrices.translate(relX, relY, relZ);

        // 面向相机（公告牌效果）
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        // 调整大小
        float scaleFactor = 0.02f;
        if (distance > 16.0) {
            // 距离越远，文字越小（可选）
            scaleFactor *= MathHelper.clamp(16.0 / distance, 0.5, 1.0);
        }
        matrices.scale(-scaleFactor, -scaleFactor, scaleFactor); // 负值翻转Y轴

        // 居中文字
        matrices.translate(-textWidth / 2.0f, -textHeight / 2.0f, 0.0f);

        // 获取最终位置矩阵
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        // 渲染文字
        textRenderer.draw(
                text,
                0,
                0,
                argb,
                false,
                positionMatrix,
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        // 恢复矩阵状态
        matrices.pop();
    }

    public static void tick() {
        ACTIVE_PARTICLES.removeIf(particle -> particle.getAge() >= particle.getMaxAge());
    }
}

package dynastxu.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dynastxu.api.ILivingEntityData;
import dynastxu.elements.AttachedElement;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.List;

import static dynastxu.elements.ElementsInfo.getTexture;

public class EntityAttachedElementsRenderer {
    public static void renderEntityOverlays(WorldRenderContext context) {
        // FIXME 当实体着火时，图标会被火焰挡住
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        Vec3d cameraPos = context.camera().getPos();
        MatrixStack matrixStack = context.matrixStack();

        // 设置渲染状态
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (LivingEntity entity : client.world.getEntitiesByClass(
                LivingEntity.class,
                client.player.getBoundingBox().expand(50),
                e -> true)) {

            if (entity == client.player) continue;
            if (!(entity instanceof ILivingEntityData)) continue;
            List<AttachedElement> elements = ((ILivingEntityData) entity).element$getAttachedElements();
            if (elements.isEmpty()) continue;

            // 计算基础位置和大小
            float distance = entity.distanceTo(client.player);
            float scale = MathHelper.clamp(1.0f - distance / 50.0f, 0.2f, 1.0f) * 0.5f;
            float time = (client.player.age + context.tickDelta()) / 20.0f;
            float yOffset = MathHelper.sin(time * 2.0f) * 0.1f;
            double height = entity.getHeight() + 0.5 + yOffset;
            Vec3d pos = entity.getLerpedPos(context.tickDelta()).add(0, height, 0);
            Vec3d transformedPos = pos.subtract(cameraPos);

            int light = LightmapTextureManager.pack(15, 15);

            // 保存矩阵状态
            matrixStack.push();
            matrixStack.translate(transformedPos.x, transformedPos.y, transformedPos.z);
            matrixStack.multiply(context.camera().getRotation());
            matrixStack.scale(scale, scale, scale);

            // 计算总宽度用于居中排列
            for (int i = 0; i < elements.size(); i++) {
                AttachedElement element = elements.get(i);
                Identifier texture = getTexture(element.getElement());
                if (texture == null) continue;

                // 计算水平偏移（居中排列）
                float offsetX = (i - (elements.size() - 1) / 2.0f) * 1.2f; // 1.2f 是图标间距

                // 为当前元素设置纹理
                RenderSystem.setShaderTexture(0, texture);

                // 准备缓冲区（每个元素单独绘制）
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();

                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);

                // 应用水平偏移
                matrixStack.push();
                matrixStack.translate(offsetX, 0, 0);
                Matrix4f modelMatrix = matrixStack.peek().getPositionMatrix();
                matrixStack.pop();

                // 顶点颜色（白色不透明）
                int color = 0xFFFFFFFF;
                float r = (color >> 16 & 0xFF) / 255.0f;
                float g = (color >> 8 & 0xFF) / 255.0f;
                float b = (color & 0xFF) / 255.0f;
                float a = (color >> 24 & 0xFF) / 255.0f;

                float halfSize = 0.5f;

                // 构建四边形（确保绘制顺序正确）
                buffer.vertex(modelMatrix, -halfSize, halfSize, 0)
                        .color(r, g, b, a)
                        .texture(0, 0)
                        .light(light)
                        .next();
                buffer.vertex(modelMatrix, halfSize, halfSize, 0)
                        .color(r, g, b, a)
                        .texture(1, 0)
                        .light(light)
                        .next();
                buffer.vertex(modelMatrix, halfSize, -halfSize, 0)
                        .color(r, g, b, a)
                        .texture(1, 1)
                        .light(light)
                        .next();
                buffer.vertex(modelMatrix, -halfSize, -halfSize, 0)
                        .color(r, g, b, a)
                        .texture(0, 1)
                        .light(light)
                        .next();

                // 提交绘制
                tessellator.draw();
            }

            // 恢复矩阵状态
            matrixStack.pop();
        }

        // 恢复渲染状态
        RenderSystem.disableBlend();
    }
}

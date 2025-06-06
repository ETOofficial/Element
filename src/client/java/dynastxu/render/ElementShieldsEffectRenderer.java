package dynastxu.render;


import dynastxu.api.ILivingEntityData;
import dynastxu.effects.ModEffects;
import dynastxu.elements.ElementsInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class ElementShieldsEffectRenderer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
//    private static Elements element;
    private static final List<RegistryEntry<StatusEffect>> TARGET_EFFECTS = List.of(
            ModEffects.HYDRO_SHIELDS_EFFECT,
            ModEffects.PYRO_SHIELDS_EFFECT,
            ModEffects.CRYO_SHIELDS_EFFECT,
            ModEffects.ELECTRO_SHIELDS_EFFECT
    );

    private static void renderScreenOverlay(DrawContext context) {
        if (client.player == null) return;

        int color = ElementsInfo.getColor(((ILivingEntityData) client.player).element$getAbsorptionElement()) - 0x99000000;

        // 获取屏幕尺寸
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // 绘制全屏半透明矩形
        context.fill(0, 0, width, height, color);

        // 如果需要纹理遮罩（可选）
        // context.drawTexture(OVERLAY_TEXTURE, 0, 0, width, height, 0, 0, 256, 256, 256, 256);
    }

    private static boolean shouldRenderOverlay() {
        if (client.player == null) return false;

        // 检测玩家是否具有目标效果且剩余时间>0
        for (RegistryEntry<StatusEffect> effect : TARGET_EFFECTS) {
//            StatusEffectInstance effectInstance = client.player.getStatusEffect(effect.value());
            if (((ILivingEntityData) client.player).element$getElementAbsorptionAmount() > 0) {
                return true;
            }
        }
        return false;
    }

    public static void render(DrawContext context) {
        if (shouldRenderOverlay()) {
            renderScreenOverlay(context);
        }
    }
}

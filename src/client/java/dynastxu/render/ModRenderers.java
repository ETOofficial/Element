package dynastxu.render;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class ModRenderers {
    public static void register() {
        WorldRenderEvents.END.register(TextParticlesTextRenderer::render);
        WorldRenderEvents.END.register(EntityAttachedElementsRenderer::renderEntityOverlays);
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> ElementShieldsEffectRenderer.render(drawContext));
    }
}

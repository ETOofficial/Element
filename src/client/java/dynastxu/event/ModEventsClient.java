package dynastxu.event;

import dynastxu.render.TextParticlesTextRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ModEventsClient {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> TextParticlesTextRenderer.tick());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("element", "text_particles_reloader");
            }

            @Override
            public void reload(ResourceManager manager) {
                TextParticlesTextRenderer.clearAllParticles();
            }
        });
    }
}

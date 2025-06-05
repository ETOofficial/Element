package dynastxu;

import dynastxu.event.ModEventsClient;
import dynastxu.network.NetworkClient;
import dynastxu.particle.ModParticleFactories;
import dynastxu.particle.ModParticles;
import dynastxu.render.ModRenderers;
import net.fabricmc.api.ClientModInitializer;

public class ElementClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        ModParticleFactories.register();
        ModParticles.register();

        ModRenderers.register();

        ModEventsClient.register();

        NetworkClient.init();

    }
}
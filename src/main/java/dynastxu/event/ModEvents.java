package dynastxu.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ModEvents {
    public static void register()
    {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            SyncEvent.sync(handler);
        });


    }
}

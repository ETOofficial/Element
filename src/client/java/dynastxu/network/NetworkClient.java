package dynastxu.network;

import dynastxu.api.ILivingEntityData;
import dynastxu.elements.AttachedElement;
import dynastxu.elements.Reactions;
import dynastxu.event.ReactionEventClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

import static dynastxu.network.Network.ELEMENT_REACTION_CHANNEL;
import static dynastxu.network.Network.ELEMENT_SYNC_CHANNEL;

public class NetworkClient {
    public static void init() {
        elementSynchronousReceptionRegister();
        elementalReactionReceivingRegister();
    }

    private static void elementSynchronousReceptionRegister() {
        // 元素同步接收
        ClientPlayNetworking.registerGlobalReceiver(
                ELEMENT_SYNC_CHANNEL,
                (client, handler, buf, responseSender) -> {
                    int entityId = buf.readVarInt();
                    NbtList list = buf.readNbt().getList("elements", NbtElement.COMPOUND_TYPE);

                    client.execute(() -> {
                        ClientWorld world = client.world;
                        if (world != null) {
                            Entity entity = world.getEntityById(entityId);
                            if (entity instanceof LivingEntity) {
                                ILivingEntityData data = (ILivingEntityData) entity;
                                List<AttachedElement> elements = new ArrayList<>();

                                for (int i = 0; i < list.size(); i++) {
                                    elements.add(AttachedElement.deserialize(list.getCompound(i)));
                                }

                                data.element$setAttachedElements(elements);
                            }
                        }
                    });
                }
        );
    }

    private static void elementalReactionReceivingRegister() {
        // 元素反应接收
        ClientPlayConnectionEvents.JOIN.register((handler1, sender, client1) -> ClientPlayNetworking.registerReceiver(ELEMENT_REACTION_CHANNEL, (client, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            Reactions reaction = buf.readEnumConstant(Reactions.class);
            float value = buf.readFloat();

            client.execute(() -> {
                Entity entity = client.world.getEntityById(entityId);
                if (entity != null) {
                    switch (reaction) {
                        case Vaporize -> ReactionEventClient.vaporateEvent(client, entity);
                        case Melt -> ReactionEventClient.meltEvent(client, entity);
                        case Overload -> ReactionEventClient.overloadEvent(client, entity);
                        case Superconduct -> ReactionEventClient.superconductEvent(client, entity, value);
                    }
                }
            });
        }));
    }


}

package dynastxu.network;

import net.minecraft.util.Identifier;

import static dynastxu.Element.MOD_ID;

public class Network {
    // 定义通信频道
    public static final Identifier ELEMENT_SYNC_CHANNEL = new Identifier(MOD_ID, "element_sync");
    public static final Identifier ELEMENT_REACTION_CHANNEL = new Identifier(MOD_ID, "element_reaction");
}

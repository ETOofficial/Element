package dynastxu.elements;

import net.minecraft.util.Identifier;

import static dynastxu.Element.MOD_ID;

public class ElementsInfo {
    public static final int PYRO_COLOR = 0xFFFE9D63;
    public static final int HYDRO_COLOR = 0xFF55DBFE;
    public static final int ANEMO_COLOR = 0xFFB6E5E8;
    public static final int ELECTRO_COLOR = 0xFFE4A7FF;
    public static final int DENDRO_COLOR = 0xFFD2F482;
    public static final int CRYO_COLOR = 0xFFA6F7F7;
    public static final int GEO_COLOR = 0xFFF4D95E;

    public static final Identifier HYDRO_TEXTURE = new Identifier(MOD_ID, "textures/elements/hydro.png");
    public static final Identifier PYRO_TEXTURE = new Identifier(MOD_ID, "textures/elements/pyro.png");
    public static final Identifier DENDRO_TEXTURE = new Identifier(MOD_ID, "textures/elements/dendro.png");
    public static final Identifier CRYO_TEXTURE = new Identifier(MOD_ID, "textures/elements/cryo.png");
    public static final Identifier GEO_TEXTURE = new Identifier(MOD_ID, "textures/elements/geo.png");
    public static final Identifier ANEMO_TEXTURE = new Identifier(MOD_ID, "textures/elements/anemo.png");
    public static final Identifier ELECTRO_TEXTURE = new Identifier(MOD_ID, "textures/elements/electro.png");

    public static int getColor(Elements element) {
        return switch (element) {
            case Pyro -> PYRO_COLOR;
            case Hydro -> HYDRO_COLOR;
            case Anemo -> ANEMO_COLOR;
            case Electro -> ELECTRO_COLOR;
            case Dendro -> DENDRO_COLOR;
            case Cryo -> CRYO_COLOR;
            case Geo -> GEO_COLOR;
        };
    }

    public static Identifier getTexture(Elements element) {
        return switch (element) {
            case Hydro -> HYDRO_TEXTURE;
            case Pyro -> PYRO_TEXTURE;
            case Dendro -> DENDRO_TEXTURE;
            case Cryo -> CRYO_TEXTURE;
            case Geo -> GEO_TEXTURE;
            case Anemo -> ANEMO_TEXTURE;
            case Electro -> ELECTRO_TEXTURE;
        };
    }
}

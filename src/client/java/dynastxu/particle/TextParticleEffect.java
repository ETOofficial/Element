package dynastxu.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class TextParticleEffect implements ParticleEffect {
    private final String displayText;
    private final int textColor;
    private final float textScale;

    public static final Codec<TextParticleEffect> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("text").forGetter(TextParticleEffect::getText),
                    Codec.INT.fieldOf("color").forGetter(TextParticleEffect::getTextColor),
                    Codec.FLOAT.fieldOf("scale").forGetter(TextParticleEffect::getTextScale)
            ).apply(instance, TextParticleEffect::new)
    );

    public TextParticleEffect(String displayText, int textColor, float textScale) {
        this.displayText = displayText;
        this.textColor = textColor;
        this.textScale = textScale;
    }




    public String getText() {
        return displayText;
    }

    public int getTextColor() {
        return textColor;
    }

    public float getTextScale() {
        return textScale;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(displayText);
        buf.writeInt(textColor);
        buf.writeFloat(textScale);
    }

    @Override
    public String asString() {
        return "element:text_particle";
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.TEXT_PARTICLE; // 注册的粒子类型
    }
}

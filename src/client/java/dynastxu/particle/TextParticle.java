package dynastxu.particle;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class TextParticle extends SpriteBillboardParticle {
    private final String displayText; // 要显示的文字
    private final int textColor;     // 文字颜色 (RGB整数)
    private final float textScale;   // 文字缩放比例
    public final TextRenderer textRenderer;

    protected TextParticle(ClientWorld world, double x, double y, double z,
                           double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider, String displayText, int textColor, float textScale, TextRenderer textRenderer) {
        super(world, x, y, z);
        this.displayText = displayText;
        this.textColor = textColor;
        this.textScale = textScale;
        this.textRenderer = textRenderer;
        this.maxAge = 100; // 粒子生命周期
        this.scale = 0.1f; // 粒子大小
        this.setSprite(spriteProvider.getSprite(age, maxAge)); // 设置粒子纹理

        this.velocityX = velocityX; // 确保速度被赋值
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT; // 粒子渲染类型
    }

    public double getPrevPosX(){
        return prevPosX;
    }
    public double getPrevPosY(){
        return prevPosY;
    }
    public double getPrevPosZ(){
        return prevPosZ;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getZ(){
        return z;
    }
    public int getAge(){
        return age;
    }
    public float getTextScale() {
        return textScale;
    }
    public int getTextColor() {
        return textColor;
    }
    public String getDisplayText() {
        return displayText;
    }

//    @Override
//    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
//
//    }

}

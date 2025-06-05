package dynastxu.elements;

import net.minecraft.nbt.NbtCompound;

public class AttachedElement {
    private Elements element;
    private float level;

    public AttachedElement(Elements element, float level) {
        this.element = element;
        this.level = level;
    }

    public Elements getElement() {
        return element;
    }

    public float getLevel() {
        return level;
    }

    public void setElement(Elements element) {
        this.element = element;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public void attenuation(float base_speed, float speed){
        if (speed == 0) return;
        if(this.level > 0){
            this.level -= ((8 * this.level / (25 * this.level + 70) + base_speed) / 20) * speed;
        }
        if(this.level < 0){
            this.level = 0;
        }
    }

    // 序列化为 NbtCompound
    public NbtCompound serialize() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("element", this.element.name()); // 保存枚举名称
        nbt.putFloat("level", this.level);
        return nbt;
    }

    // 从 NbtCompound 反序列化
    public static AttachedElement deserialize(NbtCompound nbt) {
        Elements element = Elements.valueOf(nbt.getString("element")); // 从字符串转换回枚举
        float level = nbt.getFloat("level");
        return new AttachedElement(element, level);
    }
}

package dynastxu.effects;

import dynastxu.api.ILivingEntityData;
import dynastxu.elements.AttachedElement;
import dynastxu.elements.ElementsInfo;
import dynastxu.elements.Elements;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class InstantElementEffect extends InstantStatusEffect {
    private final Elements element;
    private final float level;

    public InstantElementEffect(Elements element) {
        super(StatusEffectCategory.NEUTRAL, ElementsInfo.getColor(element));
        this.element = element;
        this.level = 0.2f;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        ((ILivingEntityData) entity).element$addAttachedElement(new AttachedElement(element, level * (amplifier + 1)));
    }
}

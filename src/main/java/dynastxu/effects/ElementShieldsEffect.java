package dynastxu.effects;

import dynastxu.api.ILivingEntityData;
import dynastxu.elements.Elements;
import dynastxu.elements.ElementsInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ElementShieldsEffect extends StatusEffect {

    private final Elements element;

    public Elements getElement() {
        return element;
    }

    protected ElementShieldsEffect(Elements element) {
        super(StatusEffectCategory.BENEFICIAL, ElementsInfo.getColor(element));
        this.element = element;
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier){
        ((ILivingEntityData) entity).element$setElementAbsorptionAmount(4 * (amplifier + 1));
        ((ILivingEntityData) entity).element$setAbsorptionElement(element);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        ((ILivingEntityData) entity).element$setElementAbsorptionAmount(0);
        ((ILivingEntityData) entity).element$setAbsorptionElement(null);
    }
}

package dynastxu.api;

import dynastxu.elements.AttachedElement;
import dynastxu.elements.Elements;
import dynastxu.elements.ReactionResult;

import java.util.List;

public interface ILivingEntityData {
    public float element$getMaxElementLevel();
    public void element$setMaxElementLevel(float maxElementLevel);
    public float element$getBaseAttenuationSpeed();
    public void element$setBaseAttenuationSpeed(float baseAttenuationSpeed);
    public float element$getAttenuationSpeed();
    public void element$setAttenuationSpeed(float attenuationSpeed);
    public float element$getElementalMastery();
    public void element$setElementalMastery(float elementalMastery);
    public float element$getElementAbsorptionAmount();
    public void element$setElementAbsorptionAmount(float elementAbsorptionAmount);
    public Elements element$getAbsorptionElement();
    public void element$setAbsorptionElement(Elements element);

    ReactionResult element$addAttachedElement(AttachedElement element);
    List<AttachedElement> element$getAttachedElements();
    void element$setAttachedElements(List<AttachedElement> elements);
    void element$syncDatas();
    ReactionResult element$reactionElements(AttachedElement element, List<AttachedElement> elements);
    void element$triggerReaction(ReactionResult reactionResult, Elements swirlElement);
}

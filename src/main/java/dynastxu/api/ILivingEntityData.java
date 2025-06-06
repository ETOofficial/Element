package dynastxu.api;

import dynastxu.elements.AttachedElement;
import dynastxu.elements.Elements;
import dynastxu.elements.ReactionResult;

import java.util.List;

public interface ILivingEntityData {
    float element$getMaxElementLevel();
    void element$setMaxElementLevel(float maxElementLevel);
    float element$getBaseAttenuationSpeed();
    void element$setBaseAttenuationSpeed(float baseAttenuationSpeed);
    float element$getAttenuationSpeed();
    void element$setAttenuationSpeed(float attenuationSpeed);
    float element$getElementalMastery();
    void element$setElementalMastery(float elementalMastery);
    float element$getElementAbsorptionAmount();
    void element$setElementAbsorptionAmount(float elementAbsorptionAmount);
    Elements element$getAbsorptionElement();
    void element$setAbsorptionElement(Elements element);
    ReactionResult element$addAttachedElement(AttachedElement element);
    List<AttachedElement> element$getAttachedElements();
    void element$setAttachedElements(List<AttachedElement> elements);
    void element$syncDatas();
    ReactionResult element$reactionElements(AttachedElement element, List<AttachedElement> elements);
    void element$triggerReaction(ReactionResult reactionResult, Elements swirlElement);
}

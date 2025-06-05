package dynastxu.api;

import dynastxu.elements.AttachedElement;
import dynastxu.elements.ReactionResult;

import java.util.List;

public interface ILivingEntityData {
    ReactionResult element$addAttachedElement(AttachedElement element);
    List<AttachedElement> element$getAttachedElements();
    void element$setAttachedElements(List<AttachedElement> elements);
    void element$syncAttachedElements();
    ReactionResult element$reactionElements(AttachedElement element, List<AttachedElement> elements);
    void element$triggerReaction(ReactionResult reactionResult);
}

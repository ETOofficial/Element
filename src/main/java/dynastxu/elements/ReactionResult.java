package dynastxu.elements;

public class ReactionResult {
    private final Reactions reaction;
    private final float reactionValue;

    public ReactionResult(Reactions reaction, float reactionValue) {
        this.reaction = reaction;
        this.reactionValue = reactionValue;
    }

    public Reactions getReaction() {
        return reaction;
    }

    public float getReactionValue() {
        return reactionValue;
    }
}

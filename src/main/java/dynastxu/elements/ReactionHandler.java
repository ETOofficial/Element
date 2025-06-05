package dynastxu.elements;

public class ReactionHandler {
    public static ReactionResult vaporize(AttachedElement hydro, AttachedElement pyro){
        if (hydro.getElement() == Elements.Pyro && pyro.getElement() == Elements.Hydro){
            return vaporize(pyro, hydro);
        } else if (hydro.getElement() != Elements.Hydro || pyro.getElement() != Elements.Pyro) {
            throw new IllegalArgumentException("Invalid element combination");
        }

        float consumeAmount = restrained(hydro, pyro);

        return new ReactionResult(Reactions.Vaporize, consumeAmount * 3);
    }

    public static ReactionResult melt(AttachedElement pyro, AttachedElement cryo){
        if (pyro.getElement() == Elements.Cryo && cryo.getElement() == Elements.Pyro){
            return melt(cryo, pyro);
        } else if (cryo.getElement() != Elements.Cryo || pyro.getElement() != Elements.Pyro) {
            throw new IllegalArgumentException("Invalid element combination");
        }

        float consumeAmount = restrained(pyro, cryo);

        return new ReactionResult(Reactions.Melt, consumeAmount * 3);
    }

    public static ReactionResult overload(AttachedElement pyro, AttachedElement electro){
        if (pyro.getElement() == Elements.Electro && electro.getElement() == Elements.Pyro){
            return overload(electro, pyro);
        } else if (pyro.getElement() != Elements.Pyro || electro.getElement() != Elements.Electro) {
            throw new IllegalArgumentException("Invalid element combination");
        }

        float consumeAmount = offset(pyro, electro);

        return new ReactionResult(Reactions.Overload, consumeAmount * 2);
    }

    public static ReactionResult superconduct(AttachedElement electro, AttachedElement cryo){
        if (electro.getElement() == Elements.Cryo && cryo.getElement() == Elements.Electro){
            return superconduct(cryo, electro);
        } else if (electro.getElement() != Elements.Electro || cryo.getElement() != Elements.Cryo) {
            throw new IllegalArgumentException("Invalid element combination");
        }

        float consumeAmount = offset(electro, cryo);

        return new ReactionResult(Reactions.Superconduct, consumeAmount * 2);
    }

    /**
     * 对两个关联元素进行克制反应，以 1:2 比例消耗
     *
     * @param element 第一个元素，克制第二个元素
     * @param restrainedElement 第二个元素，将以两倍速度被消耗
     * @return 返回本次操作中每个元素消耗量，第一个元素的消耗量为直接消耗量，第二个元素的消耗量为直接消耗量的两倍
     */
    public static float restrained(AttachedElement element, AttachedElement restrainedElement){
        // 计算可消耗量（取最小值保证比例）
        float consumeAmount = Math.min(
                element.getLevel(),
                restrainedElement.getLevel() / 2
        );

        // 按比例扣除
        element.setLevel(element.getLevel() - consumeAmount);
        restrainedElement.setLevel(restrainedElement.getLevel() - consumeAmount * 2);

        return consumeAmount;
    }

    /**
     * 对两个关联元素进行抵消反应，以 1:1 比例消耗
     *
     * @param element 第一个附加元素
     * @param element2 第二个附加元素
     * @return 返回本次操作中每个元素消耗量
     */
    public static float offset(AttachedElement element, AttachedElement element2){
        float consumeAmount = Math.min(
                element2.getLevel(),
                element.getLevel()
        );

        element2.setLevel(element2.getLevel() - consumeAmount);
        element.setLevel(element.getLevel() - consumeAmount);

        return consumeAmount;
    }
}

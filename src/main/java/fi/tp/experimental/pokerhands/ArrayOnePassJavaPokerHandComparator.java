package fi.tp.experimental.pokerhands;

public enum ArrayOnePassJavaPokerHandComparator implements PokerhandComparator {

    INSTANCE;

    @Override
    public int compareHands(PokerHand hand1, PokerHand hand2) {
        return hand1.cards().iterator().next().value() - hand2.cards().iterator().next().value();
    }

}

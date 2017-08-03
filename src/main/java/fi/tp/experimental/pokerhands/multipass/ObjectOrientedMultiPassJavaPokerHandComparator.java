package fi.tp.experimental.pokerhands.multipass;

import fi.tp.experimental.pokerhands.PokerHand;
import fi.tp.experimental.pokerhands.PokerHandComparator;

/**
 * Implements hand comparison object oriented approach and checking for each
 * hand type repeatedly scanning the cards.
 * <p>
 * Starts from the highest one (straight flush), moving down to a high card hand.
 */
public enum ObjectOrientedMultiPassJavaPokerHandComparator implements PokerHandComparator {

    INSTANCE;

    @Override
    public int compareHands(PokerHand hand1, PokerHand hand2) {

        return 0;

    }
}

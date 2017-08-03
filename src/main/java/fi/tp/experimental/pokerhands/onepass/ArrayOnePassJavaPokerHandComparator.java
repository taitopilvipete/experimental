package fi.tp.experimental.pokerhands.onepass;

import fi.tp.experimental.pokerhands.Card;
import fi.tp.experimental.pokerhands.PokerHand;
import fi.tp.experimental.pokerhands.PokerhandComparator;

import java.awt.*;
import java.util.*;

import scala.collection.JavaConverters;

/**
 * Implements hand comparison using an array that is iterated once with one card look-ahead and -behind.
 */
public enum ArrayOnePassJavaPokerHandComparator implements PokerhandComparator {

    INSTANCE;

    @Override
    public int compareHands(PokerHand hand1, PokerHand hand2) {

        Card[] cards1 = toCardArray(hand1);
        Card[] cards2 = toCardArray(hand2);

        calculateHandValueToCardArray(cards1);
        calculateHandValueToCardArray(cards2);

        return firstDifference(cards1, cards2);

    }

    private void calculateHandValueToCardArray(Card[] cards) {

        // sort first ascending
        Arrays.sort(cards, new CardComparator().asAscending());

        // detect different hands at one pass
        detectDifferentHands(1, cards,
                true, true,
                false, false,
                1);

        // at the end, sort descending before returning
        Arrays.sort(cards, new CardComparator());
    }

    private void detectDifferentHands(int index, Card[] cardArray,
                                      boolean isStraightAlive, boolean isFlushAlive,
                                      boolean isOnePairDetected, boolean isSetDetected,
                                      int sameValuesInARowCounter) {

        int sameValueBonus = 0;
        int newSameValuesInARowCounter = 1;
        boolean newIsStraightAlive = isStraightAlive;
        boolean newIsFlushAlive = isFlushAlive;
        boolean newIsOnePairDetected = isOnePairDetected;
        boolean newIsSetDetected = isSetDetected;
        boolean isLastCard = index == cardArray.length-1;

        Card currentCard = cardArray[index];

        if (index > 0) { // we're not at the first card
            Card previousCard = cardArray[index-1];
            // if previous value is same than current, continue pair-triple-quads -streak
            if (currentCard.value() == previousCard.value()) {
                newSameValuesInARowCounter = sameValuesInARowCounter + 1;
                // peek next card to see if streak ended here
                // (because we need to know if bonus should be added to this card or not)
                if (isLastCard || currentCard.value() != cardArray[index+1].value()) {
                    switch (newSameValuesInARowCounter) {
                        case 2 :
                            newIsOnePairDetected = true;
                            if (isSetDetected) {
                                // becomes a full house, BUG ALERT - bonus will be awarded to the pair, not the set : will be fixed later in this method
                                sameValueBonus = OnePassAlgorithmConstants.VALUE_BONUS_OF_FULL_HOUSE();
                            } else if (isOnePairDetected) {
                                // becomes two pair, BUG ALERT - bonus will be awarded to lower of the pairs, not the higher
                                sameValueBonus = OnePassAlgorithmConstants.VALUE_BONUS_OF_TWO_PAIR();
                            } else {
                                sameValueBonus = OnePassAlgorithmConstants.VALUE_BONUS_OF_PAIR();
                            }
                        break;
                        case 3 :
                            newIsSetDetected = true;
                            if (isOnePairDetected) {
                                // becomes a full house
                                sameValueBonus = OnePassAlgorithmConstants.VALUE_BONUS_OF_FULL_HOUSE();
                            } else {
                                sameValueBonus = OnePassAlgorithmConstants.VALUE_BONUS_OF_SET();
                            }
                        break;
                        case 4 : sameValueBonus = OnePassAlgorithmConstants.VALUE_BONUS_OF_QUADS();
                    }
                }
            }
            if (isStraightAlive && (currentCard.value() != previousCard.value() + 1)) { // straight not alive any more
                newIsStraightAlive = false;
                // but allow an ace after five
                if (currentCard.value() == 14 && previousCard.value() == 5) {
                    newIsStraightAlive = true;
                }
            }
            if (isFlushAlive && (currentCard.suit() != previousCard.suit())) {
                newIsFlushAlive = false;
            }

        }

        if (isLastCard) {
            // cards exhausted, add final multiplier for straights and better
            if (newIsStraightAlive && newIsFlushAlive) {
                sameValueBonus = OnePassAlgorithmConstants.VALUE_BONUS_OF_STRAIGHT_FLUSH();
            } else if (newIsFlushAlive) {
                sameValueBonus = OnePassAlgorithmConstants.VALUE_BONUS_OF_FLUSH();
            } else if (newIsStraightAlive) {
                sameValueBonus = OnePassAlgorithmConstants.VALUE_BONUS_OF_STRAIGHT();
            }
        }

        // replace with new card
        int newValue = sameValueBonus + currentCard.value();
        cardArray[index] = new Card(currentCard.suit(), newValue);

        // fix bug where full house value was awarded to an element of the pair, not one of the set
        if (sameValueBonus == OnePassAlgorithmConstants.VALUE_BONUS_OF_FULL_HOUSE()
                && !isOnePairDetected) { // one pair was detected at the end

            // sort to get highest (full house value) card to the top, and the set card second
            Arrays.sort(cardArray, new CardComparator());
            // award full house value to the first card, and deduct it from the second highest card
            Card cardWithFullHouseValue = cardArray[0];
            Card cardWithSetValue = cardArray[1];
            Card correctedFirstCard = new Card(cardWithFullHouseValue.suit(),
                    cardWithFullHouseValue.value()
                            - OnePassAlgorithmConstants.VALUE_BONUS_OF_FULL_HOUSE()
                            + OnePassAlgorithmConstants.VALUE_BONUS_OF_SET());
            Card correctedSecondCard = new Card(cardWithSetValue.suit(),
                    cardWithSetValue.value()
                            + OnePassAlgorithmConstants.VALUE_BONUS_OF_FULL_HOUSE()
                            - OnePassAlgorithmConstants.VALUE_BONUS_OF_SET());
            cardArray[0] = correctedFirstCard;
            cardArray[1] = correctedSecondCard;
        }

        if (!isLastCard) {
            // recurse further
            detectDifferentHands(index+1, cardArray,
                    newIsStraightAlive, newIsFlushAlive,
                    newIsOnePairDetected, newIsSetDetected,
                    newSameValuesInARowCounter);
        }

    }

    private int firstDifference(Card[] cards1, Card[] cards2) {
        for (int i = 0; i<cards1.length; i++) {
            int difference = cards1[i].value() - cards2[i].value();
            if (difference != 0) {
                return difference;
            }
        }
        return 0;
    }

    private Card[] toCardArray(PokerHand hand) {
        Collection<Card> javaCollection = JavaConverters.asJavaCollection(hand.cards());
        return javaCollection.toArray(new Card[javaCollection.size()]);
    }

    private class CardComparator implements Comparator<Card> {

        private boolean ascending = false;

        CardComparator asAscending() {
            ascending = true;
            return this;
        }

        @Override
        public int compare(Card o1, Card o2) {
            if (ascending) {
                return o1.value() - o2.value();
            } else {
                return o2.value() - o1.value();
            }
        }
    }
}

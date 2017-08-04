package fi.tp.experimental.pokerhands.multipass;

import fi.tp.experimental.pokerhands.Card;
import fi.tp.experimental.pokerhands.PokerHand;
import fi.tp.experimental.pokerhands.PokerHandComparator;
import fi.tp.experimental.pokerhands.Suit;
import fi.tp.experimental.pokerhands.javautil.CardComparator;
import fi.tp.experimental.pokerhands.javautil.ComparisonUtilsWrittenInJava;
import fi.tp.experimental.pokerhands.onepass.OnePassAlgorithmConstants;
import fi.tp.experimental.pokerhands.scalautil.ConversionUtilsWrittenInScala;

import java.util.*;

/**
 * Implements hand comparison object oriented approach and checking for each
 * hand type repeatedly scanning the cards.
 * <p>
 * Starts from the highest one (straight flush), moving down to a high card hand.
 */
public enum ObjectOrientedMultiPassJavaPokerHandComparator implements PokerHandComparator {

    INSTANCE;

    public static final Card NEUTRAL_CARD = new Card(Suit.CLUBS, 0);

    @Override
    public int compareHands(PokerHand hand1, PokerHand hand2) {

        List<Card> cardsOfHand1 = ConversionUtilsWrittenInScala.toJavaList(hand1.cards());
        List<Card> cardsOfHand2 = ConversionUtilsWrittenInScala.toJavaList(hand2.cards());

        // sort from highest to lowest for pair and straight detection
        // Execute different hand detectors, gather results in a list.
        // Each detector returns either it's VALUE_BONUS + meaningful card value OR a zero if none found.
        // After this, the algorithm appends all these together in a list of cards that are then compared.
        List<Card> detectionResults1 = detectHands(cardsOfHand1);
        List<Card> detectionResults2 = detectHands(cardsOfHand2);


        // Examples: full house detector for treys over deuces returns these meaningful values: 10000003.
        // Note that in this case the set and pair detectors will return also 10003 and 1002.
        // A straight detector only returns the highest card, e.g. ace-high straight returns 100014.
        // A high-card detector returns all the cards
        // The pair detector returns the first detected pair
        // The two-pair detector for kings and tens returns (only) the 1013 : the pair detector will return both 113 and 110
        return ComparisonUtilsWrittenInJava.INSTANCE.firstDifference(detectionResults1, detectionResults2);

    }

    private List<Card> detectHands(List<Card> cards) {

        cards.sort(new CardComparator().asAscending());

        List<Card> detected = new ArrayList<>();
        detected.addAll(detectStraightFlush(cards));
        detected.addAll(detectQuads(cards));
        detected.addAll(detectFullHouse(cards));
        detected.addAll(detectFlush(cards));
        detected.addAll(detectStraight(cards));
        detected.addAll(detectThreeOfAKind(cards));
        detected.addAll(detectTwoPair(cards));
        detected.addAll(detectPair(cards));

        // add all the cards for high card detection
        cards.sort(new CardComparator());
        detected.addAll(cards);
        return detected;
    }

    private List<Card> detectTwoPair(List<Card> cards) {

        Card repOfPair1 = NEUTRAL_CARD;
        Card repOfPair2 = NEUTRAL_CARD;

        for (Card card : cards) {
            if (occurrences(card, cards, repOfPair1) == 2) {
                if (repOfPair1 == NEUTRAL_CARD) {
                    // use repOfPair1
                    repOfPair1 = card;
                } else {
                    repOfPair2 = card;
                }
            }
        }

        // clear first pair if second not found
        if (repOfPair2 == NEUTRAL_CARD) repOfPair1 = NEUTRAL_CARD;
        List<Card> higherAndLower = Arrays.asList(repOfPair1, repOfPair2);
        higherAndLower.sort(new CardComparator());
        return higherAndLower;
    }

    private List<Card> detectPair(List<Card> cards) {
        Card pairCard = NEUTRAL_CARD;
        for (Card card : cards) {
            if (occurrences(card, cards) == 2) pairCard = card;
        }
        return Arrays.asList(pairCard);

    }

    private List<Card> detectThreeOfAKind(List<Card> cards) {
        Card setCard = NEUTRAL_CARD;
        for (Card card : cards) {
            if (occurrences(card, cards) == 3) setCard = card;
        }
        return Arrays.asList(setCard);
    }

    private List<Card> detectFullHouse(List<Card> cards) {
        Card firstCard = cards.iterator().next();
        Card lastCard = cards.get(cards.size()-1);
        Card setCard = NEUTRAL_CARD;
        Card pairCard = NEUTRAL_CARD;
        if (occurrences(firstCard, cards) == 3 && occurrences(lastCard, cards) == 2) {
            setCard = firstCard;
            pairCard = lastCard;
        } else if (occurrences(firstCard, cards) == 2 && occurrences(lastCard, cards) == 3) {
            setCard = lastCard;
            pairCard = firstCard;
        }
        return Arrays.asList(setCard, pairCard);
    }

    private List<Card> detectQuads(List<Card> cards) {

        Card quadCard = NEUTRAL_CARD;
        Card loneCard = NEUTRAL_CARD;

        if (occurrences(cards.get(0), cards) == 4) {
            quadCard = cards.get(0);
            loneCard = cards.get(cards.size()-1);
        } else if (occurrences(cards.get(1), cards) == 4) {
            quadCard = cards.get(1);
            loneCard = cards.get(cards.size()-1);
        }
        return Arrays.asList(quadCard, loneCard);
    }

    private long occurrences(Card card, List<Card> cards) {
        return occurrences(card, cards, new Card[]{});
    }
    private long occurrences(Card card, List<Card> cards, Card... excludeCards) {
        List<Card> excludeCardsList = Arrays.asList(excludeCards);
        return cards.stream().filter(card1 -> card1.value() == card.value()
            && !excludeCardsList.contains(card1)).count();
    }

    private List<Card> detectFlush(List<Card> cards) {

        boolean flushPossible = true;

        Iterator<Card> iterator = cards.iterator();
        Card currentCard = iterator.next();
        Suit flushSuit = currentCard.suit();
        while (iterator.hasNext() && flushPossible) {
            Card newCard = iterator.next();
            if (newCard.suit() != flushSuit) {
                flushPossible = false;
            }
            currentCard = newCard;
        }

        int value = 0;
        if (flushPossible) {
            value = currentCard.value();
        }

        return Arrays.asList(new Card(flushSuit, value));

    }
    private List<Card> detectStraight(List<Card> cards) {
        boolean straightPossible = true;

        Iterator<Card> iterator = cards.iterator();
        Card currentCard = iterator.next();
        while (iterator.hasNext() && straightPossible) {
            Card newCard = iterator.next();
            if (newCard.value() != currentCard.value() + 1) {
                straightPossible = false;
                if (newCard.value() == 14 && currentCard.value() == 5) {
                    straightPossible = true;
                }
            }
            currentCard = newCard;
        }

        int value = 0;
        if (straightPossible) {
            value = currentCard.value();
        }

        return Arrays.asList(new Card(currentCard.suit(), value));
    }

    private List<Card> detectStraightFlush(List<Card> cards) {

        boolean flushPossible = true;
        boolean straightPossible = true;

        Iterator<Card> iterator = cards.iterator();
        Card currentCard = iterator.next();
        Suit flushSuit = currentCard.suit();
        while (iterator.hasNext() && flushPossible && straightPossible) {
            Card newCard = iterator.next();
            if (newCard.suit() != flushSuit) {
                flushPossible = false;
            }
            if (newCard.value() != currentCard.value() + 1) {
                straightPossible = false;
                if (newCard.value() == 14 && currentCard.value() == 5) {
                    straightPossible = true;
                }
            }
            currentCard = newCard;
        }

        int value = 0;
        if (flushPossible && straightPossible) {
            value = currentCard.value();
        }

        return Arrays.asList(new Card(flushSuit, value));

    }

}

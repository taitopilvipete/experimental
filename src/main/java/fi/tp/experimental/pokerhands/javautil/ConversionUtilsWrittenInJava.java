package fi.tp.experimental.pokerhands.javautil;

import fi.tp.experimental.pokerhands.Card;
import fi.tp.experimental.pokerhands.PokerHand;
import scala.collection.JavaConverters;

import java.util.Collection;

public enum ConversionUtilsWrittenInJava {
    INSTANCE;

    public Card[] toCardArray(PokerHand hand) {
        Collection<Card> javaCollection = JavaConverters.asJavaCollection(hand.cards());
        return javaCollection.toArray(new Card[javaCollection.size()]);
    }


}

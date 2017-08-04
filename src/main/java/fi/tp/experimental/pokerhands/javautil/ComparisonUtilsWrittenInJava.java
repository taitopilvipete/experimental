package fi.tp.experimental.pokerhands.javautil;

import fi.tp.experimental.pokerhands.Card;

import java.util.List;

public enum ComparisonUtilsWrittenInJava {

    INSTANCE;

    public int firstDifference(Card[] cards1, Card[] cards2) {
        for (int i = 0; i<cards1.length; i++) {
            int difference = cards1[i].value() - cards2[i].value();
            if (difference != 0) {
                return difference;
            }
        }
        return 0;
    }

    public int firstDifference(List<Card> cards1, List<Card> cards2) {
        for (int i = 0; i<cards1.size(); i++) {
            int difference = cards1.get(i).value() - cards2.get(i).value();
            if (difference != 0) {
                return difference;
            }
        }
        return 0;
    }

}

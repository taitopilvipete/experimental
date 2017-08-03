package fi.tp.experimental.pokerhands.javautil;

import fi.tp.experimental.pokerhands.Card;

import java.util.Comparator;

public class CardComparator implements Comparator<Card> {

    private boolean ascending = false;

    public CardComparator asAscending() {
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

package fi.tp.experimental.pokerhands

case class Card(val suit : Suit, val value : Int)

// restrict to five cards? let's not
case class PokerHand(val cards : List[Card]) {

  def cardsSorted = cards.sortWith((card1, card2) => card1.value > card2.value)

  def withAnotherCard(card: Card) : PokerHand = new PokerHand(cards.::(card))
}

trait PokerhandComparator {

  /**
    * Compares the given two hands.
    * @param hand1
    * @param hand2
    * @return an integer >0 is hand1 is greater than hand2, <0 if hand2 beats hand1, 0 when hands are equal
    */
  def compareHands(hand1 : PokerHand, hand2 : PokerHand) : Int

}

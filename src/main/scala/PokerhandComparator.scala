package fi.tp.experimental.pokerhands

case class Card(val suit : Suit, val value : Byte)

// restrict to five cards? let's not
case class PokerHand(val cards : List[Card]) {
  def appendCard(card: Card) = cards.+:(card)
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

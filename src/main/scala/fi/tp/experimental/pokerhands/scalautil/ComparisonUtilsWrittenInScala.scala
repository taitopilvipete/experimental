package fi.tp.experimental.pokerhands.scalautil

import fi.tp.experimental.pokerhands.Card

object ComparisonUtilsWrittenInScala {

  // blindly assumes lists are equal length
  def firstDifference(cards1: List[Card], cards2: List[Card]): Int = cards1 match {
    case Nil => 0
    case firstCard1 :: restOfCards1 => {
      val firstCard2 = cards2.head
      val difference = firstCard1.value - firstCard2.value
      if (difference != 0) {
        difference
      } else {
        firstDifference(restOfCards1, cards2.tail)
      }
    }
  }

}

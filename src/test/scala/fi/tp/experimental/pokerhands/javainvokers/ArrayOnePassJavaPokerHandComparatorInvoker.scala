package fi.tp.experimental.pokerhands.javainvokers

import fi.tp.experimental.pokerhands.onepass.ArrayOnePassJavaPokerHandComparator
import fi.tp.experimental.pokerhands.{PokerHand, PokerHandComparator}

/**
  * Bridges java invoker to scala file.
  *
  */
object ArrayOnePassJavaPokerHandComparatorInvoker extends PokerHandComparator {

  override def compareHands(hand1: PokerHand, hand2: PokerHand): Int = {

    return ArrayOnePassJavaPokerHandComparator.INSTANCE.compareHands(hand1, hand2)
  }


}

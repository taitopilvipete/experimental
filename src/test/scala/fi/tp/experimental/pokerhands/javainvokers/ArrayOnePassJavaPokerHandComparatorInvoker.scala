package fi.tp.experimental.pokerhands.javainvokers

import fi.tp.experimental.pokerhands.{ArrayOnePassJavaPokerHandComparator, PokerHand, PokerhandComparator}

/**
  * Bridges java invoker to scala file.
  *
  */
object ArrayOnePassJavaPokerHandComparatorInvoker extends PokerhandComparator {

  override def compareHands(hand1: PokerHand, hand2: PokerHand): Int = {

    return ArrayOnePassJavaPokerHandComparator.INSTANCE.compareHands(hand1, hand2)
  }


}

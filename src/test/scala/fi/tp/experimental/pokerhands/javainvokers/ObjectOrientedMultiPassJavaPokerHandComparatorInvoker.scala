package fi.tp.experimental.pokerhands.javainvokers

import fi.tp.experimental.pokerhands.multipass.ObjectOrientedMultiPassJavaPokerHandComparator
import fi.tp.experimental.pokerhands.{PokerHand, PokerHandComparator}

/**
  * Bridges java invoker to scala file.
  *
  */
object ObjectOrientedMultiPassJavaPokerHandComparatorInvoker extends PokerHandComparator {

  override def compareHands(hand1: PokerHand, hand2: PokerHand): Int = {

    return ObjectOrientedMultiPassJavaPokerHandComparator.INSTANCE.compareHands(hand1, hand2)
  }


}

package fi.tp.experimental.pokerhands.multipass

import fi.tp.experimental.pokerhands.{PokerHand, PokerHandComparator}

/**
  * Compare hands by checking for each made hand in parallel.
  */
object ParallelMultipassScalaPokerHandComparator extends PokerHandComparator {
  /**
    * Compares the given two hands.
    *
    * @param hand1
    * @param hand2
    * @return an integer >0 is hand1 is greater than hand2, <0 if hand2 beats hand1, 0 when hands are equal
    */
  override def compareHands(hand1: PokerHand, hand2: PokerHand): Int = {

    // Execute hand detectors in parallel.
    // Let the different hand detectors emit the detected hands, gather
    // them all in one list and deduct final hand from that list.
    // (e.g. straight and flush emitted => straight flush)
    // or set + pair emitted => full house

    0
  }
}

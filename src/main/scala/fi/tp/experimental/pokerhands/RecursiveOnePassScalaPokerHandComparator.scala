package fi.tp.experimental.pokerhands

/**
  * Implements hand comparison using a slightly more complicated (vs a straightforward check for each made hand separately)
  * one-pass algorithm. Basically all the tuples (pairs, sets, quads) are detected by keeping count of how many times a
  * value is seen in a row. Stops looking for straights and flushes the moment they are no longer a possibility.
  *
  * Relies heavily on sorting the cards first, and sorting them again after calculating and adding somewhat arbitrary
  * - albeit still differentiating enough - "bonus" values (multiplicities of 10) to different made hand types.
  *
  * After these calculations, two hands are easily compared card by card for a slightly faster execution.
  *
  */
object RecursiveOnePassScalaPokerHandComparator extends PokerhandComparator {

  private val VALUE_BONUS_OF_PAIR = 100
  private val VALUE_BONUS_OF_TWO_PAIR = 1000
  private val VALUE_BONUS_OF_SET = 10000
  private val VALUE_BONUS_OF_STRAIGHT = 100000
  private val VALUE_BONUS_OF_FLUSH = 1000000
  private val VALUE_BONUS_OF_FULL_HOUSE = 10000000
  private val VALUE_BONUS_OF_QUADS = 100000000
  private val VALUE_BONUS_OF_STRAIGHT_FLUSH = 200000000

  /**
    * Compares the given two hands.
    *
    * @param hand1
    * @param hand2
    * @return an integer >0 is hand1 is greater than hand2, <0 if hand2 beats hand1, 0 when hands are equal
    */
  override def compareHands(hand1: PokerHand, hand2: PokerHand): Int = {

    def cards1: List[Card] = calculateHandValueToHand(hand1)

    def cards2: List[Card] = calculateHandValueToHand(hand2)

    return firstDifference(cards1, cards2)
  }

  private def calculateHandValueToHand(hand: PokerHand): List[Card] = {

    // sort first ascending
    val cardsSorted = hand.cardsSorted.reverse

    // detect different hands by iterating once
    val handWithValues = handleCardRecursive(new PokerHand(Nil), None, cardsSorted, true, true, false, false, 1)

    // sort after re-valuation descending
    handWithValues.cardsSorted

  }

  // blindly assumes lists are equal length
  private def firstDifference(cards1: List[Card], cards2: List[Card]): Int = cards1 match {
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

  private def handleCardRecursive(pokerHand: PokerHand,
                                  previousCardOption: Option[Card],
                                  remainingCardsList: List[Card],
                                  isStraightAlive: Boolean, isFlushAlive: Boolean,
                                  isOnePairDetected: Boolean, isSetDetected: Boolean,
                                  sameValuesInARowCounter: Int): PokerHand = {

    val currentCard = remainingCardsList.head
    val restOfCards = remainingCardsList.tail

    var sameValueBonus = 0
    var newSameValuesInARowCounter = 1
    var newIsStraightAlive = isStraightAlive
    var newIsFlushAlive = isFlushAlive
    var newIsOnePairDetected = isOnePairDetected
    var newIsSetDetected = isSetDetected

    previousCardOption match {
      case None => // fine, nothing to be done if this is the first card
      case Some(previousCard) => {
        // if previous value is same than current, continue pair-triple-quads -streak
        if (currentCard.value == previousCard.value) {
          newSameValuesInARowCounter = sameValuesInARowCounter + 1
          // peek next card to see if streak ended here
          // (because we need to know if bonus should be added to this card or not)
          if (restOfCards.isEmpty || currentCard.value != restOfCards.head.value) {
            sameValueBonus = newSameValuesInARowCounter match {
              case 2 => {
                newIsOnePairDetected = true
                if (isSetDetected) {
                  // becomes a full house, BUG ALERT - bonus will be awarded to the pair, not the set : will be fixed later in this method
                  VALUE_BONUS_OF_FULL_HOUSE
                } else if (isOnePairDetected) {
                  // becomes two pair, BUG ALERT - bonus will be awarded to lower of the pairs, not the higher
                  VALUE_BONUS_OF_TWO_PAIR
                } else {
                  VALUE_BONUS_OF_PAIR
                }
              }
              case 3 => {
                newIsSetDetected = true
                if (isOnePairDetected) {
                  // becomes a full house
                  VALUE_BONUS_OF_FULL_HOUSE
                } else {
                  VALUE_BONUS_OF_SET
                }
              }
              case 4 => VALUE_BONUS_OF_QUADS
            }
          }
        }
        if (isStraightAlive && (currentCard.value != previousCard.value + 1)) { // straight not alive any more
          newIsStraightAlive = false
          // but allow an ace after five
          if (currentCard.value == 14 && previousCard.value == 5) {
            newIsStraightAlive = true
          }
        }
        if (isFlushAlive && (currentCard.suit != previousCard.suit)) {
          newIsFlushAlive = false
        }
      }
    }

    if (restOfCards.isEmpty) {
      // cards exhausted, add final multiplier for straights and better
      if (newIsStraightAlive && newIsFlushAlive) {
        sameValueBonus = VALUE_BONUS_OF_STRAIGHT_FLUSH
      } else if (newIsFlushAlive) {
        sameValueBonus = VALUE_BONUS_OF_FLUSH
      } else if (newIsStraightAlive) {
        sameValueBonus = VALUE_BONUS_OF_STRAIGHT
      }
    }

    val newValue = sameValueBonus + currentCard.value

    val currentCardAdjusted = new Card(currentCard.suit, newValue)
    var newPokerHand = pokerHand.withAnotherCard(currentCardAdjusted)

    // fix bug where full house value was awarded to an element of the pair, not one of the set
    if (sameValueBonus == VALUE_BONUS_OF_FULL_HOUSE && !isOnePairDetected) {
      // one pair was detected at the end
      val cardsSorted = newPokerHand.cardsSorted
      // award full house value to the first card, and deduct it from the second highest card
      val firstCard = cardsSorted.head
      val secondCard = cardsSorted.tail.head
      val correctedFirstCard = new Card(firstCard.suit, firstCard.value - VALUE_BONUS_OF_FULL_HOUSE + VALUE_BONUS_OF_SET)
      val correctedSecondCard = new Card(secondCard.suit, secondCard.value + VALUE_BONUS_OF_FULL_HOUSE - VALUE_BONUS_OF_SET)
      newPokerHand = new PokerHand(List(correctedFirstCard, correctedSecondCard) ++ cardsSorted.tail.tail)
    }

    // recurse unless last card handled
    restOfCards match {
      case Nil => newPokerHand
      case card :: restOfCards => handleCardRecursive(newPokerHand,
        Some(currentCard), card :: restOfCards,
        newIsStraightAlive, newIsFlushAlive,
        newIsOnePairDetected, newIsSetDetected,
        newSameValuesInARowCounter)
    }

  }

}

package fi.tp.experimental.pokerhands

object ScalaPokerHandComparator extends PokerhandComparator {

  private val VALUE_BONUS_OF_PAIR           = 100
  private val VALUE_BONUS_OF_TWO_PAIR       = 1000
  private val VALUE_BONUS_OF_SET            = 10000
  private val VALUE_BONUS_OF_STRAIGHT       = 100000
  private val VALUE_BONUS_OF_FLUSH          = 1000000
  private val VALUE_BONUS_OF_FULL_HOUSE     = 10000000
  private val VALUE_BONUS_OF_QUADS          = 100000000
  private val VALUE_BONUS_OF_STRAIGHT_FLUSH = 200000000

  /**
    * Compares the given two hands.
    *
    * @param hand1
    * @param hand2
    * @return an integer >0 is hand1 is greater than hand2, <0 if hand2 beats hand1, 0 when hands are equal
    */
  override def compareHands(hand1: PokerHand, hand2: PokerHand): Int = {

    def cards1 : List[Card] = calculateHandValueToHand(hand1)
    def cards2 : List[Card] = calculateHandValueToHand(hand2)

    return sumOfCards(cards1) - sumOfCards(cards2)
  }



  private def sumOfCards(cards: List[Card]) : Int = {
    cards.reduce((card1, card2) => new Card(card1.suit, card1.value + card2.value)).value
  }

  private def handleCardRecursive(pokerHand : PokerHand,
                          previousCardOption: Option[Card],
                          remainingCardsList: List[Card],
                          isStraightAlive: Boolean, isFlushAlive: Boolean,
                          sameValuesInARowCounter: Int) : PokerHand = {

    val currentCard = remainingCardsList.head
    val restOfCards = remainingCardsList.tail

    var sameValueBonus = 0
    var newSameValuesInARowCounter = 1
    var newIsStraightAlive = isStraightAlive
    var newIsFlushAlive = isFlushAlive

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
              case 2 => VALUE_BONUS_OF_PAIR
              case 3 => VALUE_BONUS_OF_SET
              case 4 => VALUE_BONUS_OF_QUADS
            }
          }
        }
        if (isStraightAlive && (currentCard.value != previousCard.value - 1)) { // straight not alive any more
          newIsStraightAlive = false
          // allow a five-ball after an ace
          if (previousCard.value == 14 && currentCard.value == 5) {
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
    val newPokerHand = pokerHand.withAnotherCard(currentCardAdjusted)

    restOfCards match {
      case Nil => newPokerHand
      case card :: restOfCards => handleCardRecursive(newPokerHand,
        Some(currentCard), card :: restOfCards, newIsStraightAlive, newIsFlushAlive, newSameValuesInARowCounter)
    }

  }

  private def calculateHandValueToHand(hand: PokerHand): List[Card] = {

    // sort first
    val cardsSorted = hand.cardsSorted

    // detect different hands by iterating once
    val handWithValues = handleCardRecursive(new PokerHand(Nil), None, cardsSorted, true, true, 1)

    // calculate sums of both
    val sorted = handWithValues.cardsSorted

    // somewhat of a hack: detect full house at this level because the amount of state required
    // to detect it inside handleCardRecursive is prohibitively ugly
    val firstCard: Card = handleFullHouse(sorted)

    firstCard :: sorted.tail

  }

  private def handleFullHouse(cardsWithValues : List[Card]): Card = {

    var firstCard = cardsWithValues.head

    if (firstCard.value > VALUE_BONUS_OF_SET && firstCard.value < VALUE_BONUS_OF_STRAIGHT) {
      val secondCard = cardsWithValues.tail.head
      if (secondCard.value > VALUE_BONUS_OF_PAIR && secondCard.value < VALUE_BONUS_OF_SET) {
        // we have a confirmed full house: replace first card with the full house value added to it
        firstCard = new Card(firstCard.suit, VALUE_BONUS_OF_FULL_HOUSE - VALUE_BONUS_OF_SET + firstCard.value)
      }
    }
    firstCard
  }


}

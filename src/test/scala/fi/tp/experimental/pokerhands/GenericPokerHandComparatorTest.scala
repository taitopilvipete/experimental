package fi.tp.experimental.pokerhands

trait GenericPokerHandComparatorTest {

  private val RUN_TESTS_WITH_ALL_PERMUTATIONS = true // set to true to run tests with all permutations of the hands

  def runTestsForHands(compareFunction: (PokerHand, PokerHand) => Int)(hand1: PokerHand, hand2: PokerHand, expectedResult: Char) = {
    if (RUN_TESTS_WITH_ALL_PERMUTATIONS) {
      testAllPermutations(compareFunction)(hand1, hand2, expectedResult)
    } else {
      testBothWays(compareFunction)(hand1, hand2, expectedResult)
    }
  }

  def testAllPermutations(compareFunction: (PokerHand, PokerHand) => Int)(hand1: PokerHand, hand2: PokerHand, expectedResult: Char) = {
    val permutationsOfHand1 : List[PokerHand] = permutate(hand1)
    val permutationsOfHand2 : List[PokerHand] = permutate(hand2)

    permutationsOfHand1.foreach(
      hand1 => permutationsOfHand2.foreach(
        hand2 => testBothWays(compareFunction)(hand1, hand2, expectedResult)))
  }

  def testBothWays(compareFunction: (PokerHand, PokerHand) => Int)(hand1: PokerHand, hand2: PokerHand, expectedResult: Char) = {

    val comparisonValue1 = compareFunction(hand1, hand2)
    val comparisonValue2 = compareFunction(hand2, hand1)

    expectedResult match {
      case '>' => {
        assert(comparisonValue1 > 0)
        assert(comparisonValue2 < 0)
      }
      case '<' => {
        assert(comparisonValue1 < 0)
        assert(comparisonValue2 > 0)
      }
      case '=' => {
        assert(comparisonValue1 == 0)
        assert(comparisonValue2 == 0)
      }
    }
  }

  def permutateOtherCardsOfHand(allCards: List[Card], cardsToExclude: List[Card]): List[PokerHand] = {

    // if there's only one card to pick, just return the hand
    val allowedCards = allCards.filter(card => !cardsToExclude.contains(card))
    if (allowedCards.size == 1) {
      List(new PokerHand(allowedCards.head :: cardsToExclude))
    }
    // otherwise permute the rest recursively
    else  {
      allowedCards.flatMap(card => permutateOtherCardsOfHand(allCards, card :: cardsToExclude))
    }
  }

  def permutate(hand: PokerHand): List[PokerHand] = {
    hand.cards.flatMap(card => permutateOtherCardsOfHand(hand.cards, List(card)))
  }


}

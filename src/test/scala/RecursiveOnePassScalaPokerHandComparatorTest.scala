package fi.tp.experimental.pokerhands

import org.scalatest.FunSuite
import RecursiveOnePassScalaPokerHandComparator._
import PokerHandConverter.handFromString

class RecursiveOnePassScalaPokerHandComparatorTest extends FunSuite {

  private val RUN_TESTS_WITH_ALL_PERMUTATIONS = true // set to true to run tests with all permutations of the hands

  test("Ace high beats king high") {
    runTestsForHands(handFromString("As Qc Jh 7s 2c"), handFromString("Kd 5h 4c 3s 2s"), '>')
  }

  test("Jack high equals another Jack high with equal kickers down the way") {
    runTestsForHands(handFromString("8s 9c Jh 7s 2c"), handFromString("8d 9c Jc 7s 2c"), '=')
  }

  test("Pair of kings beats ace high") {
    runTestsForHands(handFromString("As Qc Jh 7s 2c"), handFromString("Kd 5h 4c Ks 2s"), '<')
  }

  test("Small pair beats big cards") {
    runTestsForHands(handFromString("As Qc Jh Ks 9c"), handFromString("2d 5h 4c 3s 2s"), '<')
  }

  test("Pair of kings beats pair of eights") {
    runTestsForHands(handFromString("Ks Qc Jh Kc 2c"), handFromString("Kd 8h 4c As 8s"), '>')
  }

  test("Pair of kings with Q kicker beats pair of kings with J kicker") {
    runTestsForHands(handFromString("Kd 5h 4c Ks Qs"), handFromString("Kd 5h 4c Ks Js"), '>')
  }

  test("Small two pair beats big pair") {
    runTestsForHands(handFromString("As Qc Ah Ks 9c"), handFromString("2d 3h 4c 3s 2s"), '<')
  }

  test("When the two pairs are the same, kicker counts") {
    runTestsForHands(handFromString("2d 3h Qc 3s 2s"), handFromString("2d 3h Tc 3s 2s"), '>')
  }

  test("Three of a kind beats two pair") {
    runTestsForHands(handFromString("As Kc 3h 3s 3c"), handFromString("Ah Ad Kc Kd Th"), '>')
  }

  test("Straight beats three of a kind") {
    runTestsForHands(handFromString("5s 4c 3h 2s 6c"), handFromString("Ah Ad Ac Jd Th"), '>')
  }

  test("Ace counts as 1 in a straight") {
    runTestsForHands(handFromString("5s 4c 3h 2s Ac"), handFromString("Ah Ad Ac Jd Th"), '>')
  }

  test("Equal straights are equal") {
    runTestsForHands(handFromString("As Kc Qh Js Tc"), handFromString("Ah Kd Qc Jd Th"), '=')
  }

  test("Flush beats straight") {
    runTestsForHands(handFromString("As Ks Qs Js 9s"), handFromString("Ah Kd Qc Jd Th"), '>')
  }

  test("Full house beats flush") {
    runTestsForHands(handFromString("As Ac Ah Ks Kc"), handFromString("As Ks Qs Js 9s"), '>')
  }

  test("Small full house beats big flush") {
    runTestsForHands(handFromString("2s 3c 2h 3s 2c"), handFromString("As Ks Qs Js 9s"), '>')
  }

  test("Bigger full house beats small full house") {
    runTestsForHands(handFromString("2s Ac 2h As Ad"), handFromString("As Ac Kh Kc Kd"), '>')
  }

  test("Full house beats seven high") {
    runTestsForHands(handFromString("As Ac Ah Ks Kc"), handFromString("7d 5h 4c 3s 2s"), '>')
  }

  test("Even a small full house beats ace high") {
    runTestsForHands(handFromString("2s 3c 2h 3s 2c"), handFromString("Ad Kh Qc Js 2s"), '>')
  }

  test("Four of a kind beats full house") {
    runTestsForHands(handFromString("As Ac Ah Ks Kc"), handFromString("5s 5d 5h 5c 9s"), '<')
  }

  test("Straight flush beats four of a kind") {
    runTestsForHands(handFromString("As Ks Qs Js Ts"), handFromString("5s 5d 5h 5c 9s"), '>')
  }

  test("Equal straight flushes are equal") {
    runTestsForHands(handFromString("Ts Ks Qs Js 9s"), handFromString("Th Kh Qh Jh 9h"), '=')
  }

  test("Ace counts as 1 in straight flushes") {
    runTestsForHands(handFromString("As 2s 3s 4s 5s"), handFromString("5s 5d 5h 5c 9s"), '>')
  }

  test("Bigger two pair beats smaller two pair") {
    runTestsForHands(handFromString("Qs Qc 2h 2s 9c"), handFromString("Jd Jh Tc Ts As"), '>')
  }


  def runTestsForHands(hand1: PokerHand, hand2: PokerHand, expectedResult: Char) = {
    if (RUN_TESTS_WITH_ALL_PERMUTATIONS) {
      testAllPermutations(hand1, hand2, expectedResult)
    } else {
      testBothWays(hand1, hand2, expectedResult)
    }
  }

  def testAllPermutations(hand1: PokerHand, hand2: PokerHand, expectedResult: Char) = {
    val permutationsOfHand1 : List[PokerHand] = permutate(hand1)
    val permutationsOfHand2 : List[PokerHand] = permutate(hand2)

    permutationsOfHand1.foreach(
      hand1 => permutationsOfHand2.foreach(
        hand2 => testBothWays(hand1, hand2, expectedResult)))
  }

  def testBothWays(hand1: PokerHand, hand2: PokerHand, expectedResult: Char) = {

    val comparisonValue1 = compareHands(hand1, hand2)
    val comparisonValue2 = compareHands(hand2, hand1)

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

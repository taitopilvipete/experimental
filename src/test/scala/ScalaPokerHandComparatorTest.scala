package fi.tp.experimental.pokerhands

import org.scalatest.FunSuite
import ScalaPokerHandComparator._
import PokerHandConverter.handFromString

class ScalaPokerHandComparatorTest extends FunSuite {

  test("Ace high beats king high") {
    testBothWays(handFromString("As Qc Jh 7s 2c"), handFromString("Kd 5h 4c 3s 2s"), '>')
  }

  test("Jack high equals another Jack high with equal kickers down the way") {
    testBothWays(handFromString("8s 9c Jh 7s 2c"), handFromString("8d 9c Jc 7s 2c"), '=')
  }

  test("Pair of kings beats ace high") {
    testBothWays(handFromString("As Qc Jh 7s 2c"), handFromString("Kd 5h 4c Ks 2s"), '<')
  }

  test("Small pair beats big cards") {
    testBothWays(handFromString("As Qc Jh Ks 9c"), handFromString("2d 5h 4c 3s 2s"), '<')
  }

  test("Pair of kings beats pair of eights") {
    testBothWays(handFromString("Ks Qc Jh Kc 2c"), handFromString("Kd 8h 4c As 8s"), '>')
  }

  test("Pair of kings with Q kicker beats pair of kings with J kicker") {
    testBothWays(handFromString("Kd 5h 4c Ks Qs"), handFromString("Kd 5h 4c Ks Js"), '>')
  }

  test("Three of a kind beats two pair") {
    testBothWays(handFromString("As Kc 3h 3s 3c"), handFromString("Ah Ad Kc Kd Th"), '>')
  }

  test("Straight beats three of a kind") {
    testBothWays(handFromString("5s 4c 3h 2s 6c"), handFromString("Ah Ad Ac Jd Th"), '>')
  }

  test("Ace counts as 1 in a straight") {
    testBothWays(handFromString("5s 4c 3h 2s Ac"), handFromString("Ah Ad Ac Jd Th"), '>')
  }

  test("Equal straights are equal") {
    testBothWays(handFromString("As Kc Qh Js Tc"), handFromString("Ah Kd Qc Jd Th"), '=')
  }

  test("Flush beats straight") {
    testBothWays(handFromString("As Ks Qs Js 9s"), handFromString("Ah Kd Qc Jd Th"), '>')
  }

  test("Full house beats flush") {
    testBothWays(handFromString("As Ac Ah Ks Kc"), handFromString("As Ks Qs Js 9s"), '>')
  }

  test("Small house beats flush") {
    testBothWays(handFromString("2s 3c 2h 3s 2c"), handFromString("As Ks Qs Js 9s"), '>')
  }

  test("Full house beats seven high") {
    testBothWays(handFromString("As Ac Ah Ks Kc"), handFromString("7d 5h 4c 3s 2s"), '>')
  }

  test("Even a small full house beats ace high") {
    testBothWays(handFromString("2s 3c 2h 3s 2c"), handFromString("Ad Kh Qc Js 2s"), '>')
  }

  test("Four of a kind beats full house") {
    testBothWays(handFromString("As Ac Ah Ks Kc"), handFromString("5s 5d 5h 5c 9s"), '<')
  }

  test("Straight flush beats four of a kind") {
    testBothWays(handFromString("As Ks Qs Js Ts"), handFromString("5s 5d 5h 5c 9s"), '>')
  }

  test("Equal straight flushes are equal") {
    testBothWays(handFromString("Ts Ks Qs Js 9s"), handFromString("Th Kh Qh Jh 9h"), '=')
  }

  test("Ace counts as 1 in straight flushes") {
    testBothWays(handFromString("As 2s 3s 4s 5s"), handFromString("5s 5d 5h 5c 9s"), '>')
  }

  def testBothWays(hand1: PokerHand, hand2: PokerHand, operator: Char) = {
    val comparisonValue1 = compareHands(hand1, hand2);
    val comparisonValue2 = compareHands(hand2, hand1);
    operator match {
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

}

package fi.tp.experimental.pokerhands.onepass

import fi.tp.experimental.pokerhands.GenericPokerHandComparatorTest
import fi.tp.experimental.pokerhands.PokerHandConverter.handFromString
import org.scalatest.FunSuite

class RecursiveOnePassScalaPokerHandComparatorTest extends FunSuite with GenericPokerHandComparatorTest {

  val handComparatorFunction = RecursiveOnePassScalaPokerHandComparator.compareHands _

  test("Ace high beats king high") {
    runTestsForHands(handComparatorFunction)(handFromString("As Qc Jh 7s 2c"), handFromString("Kd 5h 4c 3s 2s"), '>')
  }

  test("Jack high equals another Jack high with equal kickers down the way") {
    runTestsForHands(handComparatorFunction)(handFromString("8s 9c Jh 7s 2c"), handFromString("8d 9c Jc 7s 2c"), '=')
  }

  test("Pair of kings beats ace high") {
    runTestsForHands(handComparatorFunction)(handFromString("As Qc Jh 7s 2c"), handFromString("Kd 5h 4c Ks 2s"), '<')
  }

  test("Small pair beats big cards") {
    runTestsForHands(handComparatorFunction)(handFromString("As Qc Jh Ks 9c"), handFromString("2d 5h 4c 3s 2s"), '<')
  }

  test("Pair of kings beats pair of eights") {
    runTestsForHands(handComparatorFunction)(handFromString("Ks Qc Jh Kc 2c"), handFromString("Kd 8h 4c As 8s"), '>')
  }

  test("Pair of kings with Q kicker beats pair of kings with J kicker") {
    runTestsForHands(handComparatorFunction)(handFromString("Kd 5h 4c Ks Qs"), handFromString("Kd 5h 4c Ks Js"), '>')
  }

  test("Small two pair beats big pair") {
    runTestsForHands(handComparatorFunction)(handFromString("As Qc Ah Ks 9c"), handFromString("2d 3h 4c 3s 2s"), '<')
  }

  test("When the two pairs are the same, kicker counts") {
    runTestsForHands(handComparatorFunction)(handFromString("2d 3h Qc 3s 2s"), handFromString("2d 3h Tc 3s 2s"), '>')
  }

  test("Three of a kind beats two pair") {
    runTestsForHands(handComparatorFunction)(handFromString("As Kc 3h 3s 3c"), handFromString("Ah Ad Kc Kd Th"), '>')
  }

  test("Straight beats three of a kind") {
    runTestsForHands(handComparatorFunction)(handFromString("5s 4c 3h 2s 6c"), handFromString("Ah Ad Ac Jd Th"), '>')
  }

  test("Ace counts as 1 in a straight") {
    runTestsForHands(handComparatorFunction)(handFromString("5s 4c 3h 2s Ac"), handFromString("Ah Ad Ac Jd Th"), '>')
  }

  test("Equal straights are equal") {
    runTestsForHands(handComparatorFunction)(handFromString("As Kc Qh Js Tc"), handFromString("Ah Kd Qc Jd Th"), '=')
  }

  test("Flush beats straight") {
    runTestsForHands(handComparatorFunction)(handFromString("As Ks Qs Js 9s"), handFromString("Ah Kd Qc Jd Th"), '>')
  }

  test("Full house beats flush") {
    runTestsForHands(handComparatorFunction)(handFromString("As Ac Ah Ks Kc"), handFromString("As Ks Qs Js 9s"), '>')
  }

  test("Small full house beats big flush") {
    runTestsForHands(handComparatorFunction)(handFromString("2s 3c 2h 3s 2c"), handFromString("As Ks Qs Js 9s"), '>')
  }

  test("Bigger full house beats small full house") {
    runTestsForHands(handComparatorFunction)(handFromString("2s Ac 2h As Ad"), handFromString("As Ac Kh Kc Kd"), '>')
  }

  test("Full house beats seven high") {
    runTestsForHands(handComparatorFunction)(handFromString("As Ac Ah Ks Kc"), handFromString("7d 5h 4c 3s 2s"), '>')
  }

  test("Even a small full house beats ace high") {
    runTestsForHands(handComparatorFunction)(handFromString("2s 3c 2h 3s 2c"), handFromString("Ad Kh Qc Js 2s"), '>')
  }

  test("Four of a kind beats full house") {
    runTestsForHands(handComparatorFunction)(handFromString("As Ac Ah Ks Kc"), handFromString("5s 5d 5h 5c 9s"), '<')
  }

  test("Straight flush beats four of a kind") {
    runTestsForHands(handComparatorFunction)(handFromString("As Ks Qs Js Ts"), handFromString("5s 5d 5h 5c 9s"), '>')
  }

  test("Equal straight flushes are equal") {
    runTestsForHands(handComparatorFunction)(handFromString("Ts Ks Qs Js 9s"), handFromString("Th Kh Qh Jh 9h"), '=')
  }

  test("Ace counts as 1 in straight flushes") {
    runTestsForHands(handComparatorFunction)(handFromString("As 2s 3s 4s 5s"), handFromString("5s 5d 5h 5c 9s"), '>')
  }

  test("Bigger two pair beats smaller two pair") {
    runTestsForHands(handComparatorFunction)(handFromString("Qs Qc 2h 2s 9c"), handFromString("Jd Jh Tc Ts As"), '>')
  }

}

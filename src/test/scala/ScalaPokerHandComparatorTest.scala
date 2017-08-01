package fi.tp.experimental.pokerhands

import org.scalatest.FunSuite
import ScalaPokerHandComparator._
import PokerHandConverter.handFromString

class ScalaPokerHandComparatorTest extends FunSuite {

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

  test("Full house beats seven high") {
    testBothWays(handFromString("As Ac Ah Ks Kc"), handFromString("7d 5h 4c 3s 2s"), '>')
  }
}

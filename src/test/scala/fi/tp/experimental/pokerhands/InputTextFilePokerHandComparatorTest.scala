package fi.tp.experimental.pokerhands

import fi.tp.experimental.pokerhands.onepass.RecursiveOnePassScalaPokerHandComparator
import fi.tp.experimental.pokerhands.javainvokers.{ArrayOnePassJavaPokerHandComparatorInvoker, ObjectOrientedMultiPassJavaPokerHandComparatorInvoker}
import fi.tp.experimental.pokerhands.multipass.ParallelMultipassScalaPokerHandComparator
import org.scalatest.FunSuite

import scala.io.Source

class InputTextFilePokerHandComparatorTest extends FunSuite with GenericPokerHandComparatorTest {

  test("Run tests from file with RecursiveOnePassScalaPokerHandComparator") {
    def comparatorFunction = RecursiveOnePassScalaPokerHandComparator.compareHands _

    runTestsFromFile(comparatorFunction _)
  }

  test("Run tests from file with ArrayOnePassJavaPokerHandComparator") {
    def comparatorFunction = ArrayOnePassJavaPokerHandComparatorInvoker.compareHands  _

    runTestsFromFile(comparatorFunction _)
  }

  test("Run tests from file with ParallelMultipassScalaPokerHandComparator") {
    def comparatorFunction = ParallelMultipassScalaPokerHandComparator.compareHands  _

    runTestsFromFile(comparatorFunction _)
  }

  test("Run tests from file with ObjectOrientedMultipassJavaPokerHandComparator") {
    def comparatorFunction = ObjectOrientedMultiPassJavaPokerHandComparatorInvoker.compareHands  _

    runTestsFromFile(comparatorFunction _)
  }

  private def runTestsFromFile(compareFunction: () => (PokerHand, PokerHand) => Int) = {
    Source.fromFile("src/test/resources/testhands1.csv").getLines().foreach(line => {
      testHands(compareFunction())(line)
    })
  }

  def testHands(compareFunction: (PokerHand, PokerHand) => Int)(handsLine: String) = {

    println("Running test " + handsLine)

    // parse line in the format As Kc Td 9c 8h > Kh 7c 5d 4h 2s  # simple high
    val handsWithoutComment = handsLine.split("#")
    val comment = handsWithoutComment.reverse.head
    val handsAndExpectedResult = handsWithoutComment.head
    val hands = handsAndExpectedResult.split("[<>=]")
    if (hands.length != 2) {
      println("Skipping line because couldn't split into hands.\n")
    } else {
      val handString1 = hands.head
      val handString2 = hands.reverse.head
      val expectedResult = handsAndExpectedResult.charAt(handString1.length)
      val hand1: PokerHand = PokerHandConverter.handFromString(handString1)
      val hand2: PokerHand = PokerHandConverter.handFromString(handString2)
      runTestsForHands(compareFunction)(hand1, hand2, expectedResult, comment)
    }
  }


}

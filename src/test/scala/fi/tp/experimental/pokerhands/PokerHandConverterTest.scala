package fi.tp.experimental.pokerhands

import fi.tp.experimental.pokerhands.PokerHandConverter.handFromString
import fi.tp.experimental.pokerhands.Suit._
import org.scalatest.FunSuite

class PokerHandConverterTest extends FunSuite {

  test("Simple hand from String") {
    val hand = handFromString("As Jc Kh Qs 7d")

    assert(hand.cardsSorted == List(Card(SPADES,14), Card(HEARTS,13), Card(SPADES,12), Card(CLUBS,11), Card(DIAMONDS,7)))
  }

  test("Camelcase and varying spaces hand from String") {
    val hand = handFromString("Asjc   Khqs   7d")

    assert(hand.cardsSorted == List(Card(SPADES,14), Card(HEARTS,13), Card(SPADES,12), Card(CLUBS,11), Card(DIAMONDS,7)))
  }

}

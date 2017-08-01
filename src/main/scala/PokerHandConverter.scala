package fi.tp.experimental.pokerhands

object PokerHandConverter {

  /**
    * Converts a String to a PokerHand.
    *
    * TODO sanity checks
    *
    * @param handString a 5-card hand in the String form, e.g. As Ac Jh Td 7s (order not important)
    * @return
    */
  def handFromString(handString: String): PokerHand = {
    def hand = new PokerHand(Nil)

    // initialize empty hand
    val handChars: List[Char] = handString.toList
    recursivelyConstructHandFromString(handChars, hand)
  }

  // helper to convert char to byte value
  private def valueOfChar(firstChar: Char): Option[Byte] = {
    firstChar match {
      case 'A' => Some(14)
      case 'a' => Some(14)
      case 'K' => Some(13)
      case 'k' => Some(13)
      case 'Q' => Some(12)
      case 'q' => Some(12)
      case 'J' => Some(11)
      case 'j' => Some(11)
      case 'T' => Some(10)
      case 't' => Some(10)
      case '9' => Some(9)
      case '8' => Some(8)
      case '7' => Some(7)
      case '6' => Some(6)
      case '5' => Some(5)
      case '4' => Some(4)
      case '3' => Some(3)
      case '2' => Some(2)
      case _ => None
    }
  }


  // helper to convert char to a Suit
  private def suitOfChar(char: Char) : Suit = char match {
    case 'h' => Suit.HEARTS
    case 'd' => Suit.DIAMONDS
    case 'c' => Suit.CLUBS
    case 's' => Suit.SPADES
  }

  // private recursive helper
  private def recursivelyConstructHandFromString(restOfHand: List[Char], hand: PokerHand): PokerHand = {
    restOfHand match {
      case Nil => hand
      case firstChar :: tail => {
        val valueOption = valueOfChar(firstChar)
        valueOption match {
          case Some(value) => {
            val suit = suitOfChar(tail.head)
            val card = new Card(suit, value)
            hand.appendCard(card)
          }
          case None => // fine, do nothing
        }
        recursivelyConstructHandFromString(tail.tail, hand)
      }
    }
  }



}

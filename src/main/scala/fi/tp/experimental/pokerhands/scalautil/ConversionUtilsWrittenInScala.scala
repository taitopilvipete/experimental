package fi.tp.experimental.pokerhands.scalautil

import fi.tp.experimental.pokerhands.Card

object ConversionUtilsWrittenInScala {

  def toJavaList(cards : List[Card]) : java.util.List[Card] = {
    val javaList : java.util.List[Card] = new java.util.ArrayList(cards.size)
    cards.foreach(card => javaList.add(card))
    javaList
  }

}

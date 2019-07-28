package org.verdande.core

import java.time.{Duration, Instant}

trait TimestampProvider {
  def timestamp(): Double
}

object TimestampProvider {
  implicit val systemTime = new TimestampProvider {
    override def timestamp: Double = Instant.now().getEpochSecond
  }
}

case class PeriodStart(value: Long) extends AnyVal

trait PeriodProvider {
  def mark: PeriodStart

  def durationFrom(startPoint: PeriodStart): Double = {
    val finishPoint = System.nanoTime()
    (finishPoint - startPoint.value).toDouble / 1e9
  }
}

object PeriodProvider {
  /**
    * System time provider is accurate to one milisecond
    */
  implicit val nanosecondPeriodProvider = new PeriodProvider {
    override def mark: PeriodStart = PeriodStart(System.nanoTime())
  }
}

package org.verdande.core

import java.time.{Duration, Instant}

trait TimeProvider {

  def now: Instant

  def timestamp: Double = now.getEpochSecond

  def durationFrom(point: Instant): Double = {
    val duration = Duration.between(point, now)
    duration.getSeconds + (duration.getNano / 1e9)
  }
}

object TimeProvider {
  implicit val systemTime = new TimeProvider {
    override def now: Instant = Instant.now()
  }
}

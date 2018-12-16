package org.verdande.core

import java.time.{Duration, Instant}

trait Gauge {
  def inc(): Unit

  def inc(value: Double): Unit

  def dec(): Unit

  def dec(value: Double): Unit

  def set(value: Double): Unit

  def setToCurrentTime(): Unit = {
    set(Instant.now.getEpochSecond)
  }

  def time[A](f: => A) = {
    val start = Instant.now()
    try {
      f
    } finally {
      val stop = Instant.now()
      val duration = Duration.between(start, stop)
      val result = duration.getSeconds + (duration.getNano / 1e9)
      set(result)
    }
  }
}

case class GaugeMetric(name: String,
                       description: String,
                       labelsKeys: Seq[String]) extends Gauge with Collector with Metric {

  var buffer: Double = 0.0

  override def inc(): Unit = buffer += 1.0

  override def inc(value: Double): Unit = {
    buffer += value
  }

  override def dec(): Unit = buffer -= 1.0

  override def dec(value: Double): Unit = buffer -= value

  override def set(value: Double): Unit = buffer = value

  override def collect(): Sample = Sample(this, Series(Seq.empty, buffer))
}

object Gauge {
  def build(name: String, description: String): GaugeMetric = {
    new GaugeMetric(name, description, Seq.empty)
  }
}




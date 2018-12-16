package org.verdande.core

import java.time.{Duration, Instant}
import java.util.concurrent.atomic.DoubleAdder

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

  def track[A](f: => A): A = {
    inc()
    try {
      f
    } finally {
      dec()
    }
  }
}

case class GaugeMetric(name: String,
                       description: String,
                       labelsKeys: Seq[String]) extends Gauge with Collector with Metric {

  val incs: DoubleAdder = new DoubleAdder()
  val decs: DoubleAdder = new DoubleAdder()

  override def inc(): Unit = incs.add(1.0)

  override def inc(value: Double): Unit = incs.add(value)


  override def dec(): Unit = decs.add(1.0)

  override def dec(value: Double): Unit = decs.add(value)

  override def set(value: Double): Unit = {
    incs.reset()
    decs.reset()
    incs.add(value)
  }

  override def collect(): Sample = Sample(this, Series(Seq.empty, incs.doubleValue() - decs.doubleValue()))
}

object Gauge {
  def build(name: String, description: String): GaugeMetric = {
    new GaugeMetric(name, description, Seq.empty)
  }
}




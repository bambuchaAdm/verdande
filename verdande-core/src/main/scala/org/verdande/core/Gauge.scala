package org.verdande.core

trait Gauge {
  def inc(): Unit
  def inc(value: Double): Unit
  def dec(): Unit
  def dec(value: Double): Unit
  def set(value: Double): Unit
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

  override def set(value: Double): Unit = ???

  override def collect(): Sample = Sample(this, Series(Seq.empty, buffer))
}

object Gauge {
  def build(name: String, description: String): GaugeMetric = {
    new GaugeMetric(name, description, Seq.empty)
  }
}




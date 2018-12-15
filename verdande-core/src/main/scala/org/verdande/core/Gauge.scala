package org.verdande.core

trait Gauge {
  def inc(): Unit
  def inc(value: Double): Unit
  def dec(): Unit
  def dec(value: Double): Unit
  def set(value: Double): Unit
}

case class GaugeMetric() extends Gauge with Collector {
  override def inc(): Unit = ???

  override def inc(value: Double): Unit = ???

  override def dec(): Unit = ???

  override def dec(value: Double): Unit = ???

  override def set(value: Double): Unit = ???

  override def collect(): Sample = ???
}

object Gauge {
  def build(name: String, description: String): GaugeMetric = {
    new GaugeMetric()
  }
}




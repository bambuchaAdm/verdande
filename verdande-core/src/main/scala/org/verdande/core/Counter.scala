package org.verdande.core

trait Counter {
  def inc()
  def inc(value: Double)
}

class CounterMetric(val name: String, val description: String) extends Counter {
  var buffer = 0.0

  override def inc(): Unit = buffer += 1

  override def inc(value: Double): Unit = {
    if (value < 0) {
      throw new IllegalArgumentException("counter increase by negative value")
    }
    buffer += value
  }

  def value: Double = buffer
}

object Counter {
  def build(name: String, description: String): CounterMetric = new CounterMetric(name, description)
}
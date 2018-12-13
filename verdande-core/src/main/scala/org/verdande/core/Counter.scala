package org.verdande.core

trait Counter {
  def inc()
  def inc(value: Double)
  def value: Double
}

object Counter {
  def apply(): Counter = new Counter(){

    var buffer = 0.0

    override def inc(): Unit = buffer += 1

    override def inc(value: Double): Unit = {
      if (value < 0) {
        throw new IllegalArgumentException("counter increase by negative value")
      }
      buffer += value
    }

    override def value: Double = buffer
  }
}
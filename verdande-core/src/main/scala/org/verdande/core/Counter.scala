package org.verdande.core

import java.util.concurrent.atomic.{AtomicReference, DoubleAdder}

import scala.annotation.tailrec

trait Counter {
  def inc()
  def inc(value: Double)
}

class CounterChild(val labelValues: List[String]) extends Counter {
  val buffer = new DoubleAdder()

  override def inc(): Unit = buffer.add(1.0)

  override def inc(value: Double): Unit = {
    if (value < 0) {
      throw new IllegalArgumentException("counter increase by negative value")
    }
    buffer.add(value)
  }

  def value: Double = buffer.sum()
}

class CounterMetric(val name: String, val description: String, val labelsNames: Seq[String])
  extends Counter with Metric with Collector with Labelable[Counter] {

  private val children = new AtomicReference[Map[Labels, CounterChild]](Map.empty)

  private val noLabel = new CounterChild(List.empty)

  override def inc(): Unit = noLabel.inc()

  override def inc(value: Double): Unit = noLabel.inc(value)

  @tailrec
  override final def labels(value: Labels): Counter = {
    val map = children.get()
    map.get(value) match {
      case Some(counter) => counter
      case None =>
        val counter = new CounterChild(value.values)
        val next = map.updated(value, counter)
        if(children.compareAndSet(map, next)){
          counter
        } else {
          labels(value)
        }
    }
  }

  override def collect(): Sample = {
    val childSeries: List[Series] = children.get().map {
      case (_, value) => SimpleSeries(value.labelValues, value.value)
    }(collection.breakOut)
    val voidSeries = SimpleSeries(noLabel.labelValues, noLabel.value)

    SimpleSample(this, voidSeries :: childSeries)
  }

  def register()(implicit registry: CollectorRegistry): CounterMetric = {
    registry.register(this)
    this
  }
}

object Counter {
  def build(name: String, description: String, labelsKeys: Seq[String] = Seq.empty): CounterMetric =
    new CounterMetric(name, description, labelsKeys)
}
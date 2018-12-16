package org.verdande.core

import java.util.concurrent.atomic.{AtomicReference, DoubleAdder}

import scala.annotation.tailrec
import scala.util.control.NonFatal

trait Counter {
  def inc()
  def inc(value: Double)

  def countExceptions[A](exceptions: Class[_ <: Throwable]*)(f: => A): A = {
    try {
      f
    } catch {
      case NonFatal(e) if exceptions.isEmpty || exceptions.exists(_.isInstance(e)) =>
        inc()
        throw e
    }
  }
}

private[core] class CounterChild(val labelValues: List[String]) extends Counter {
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

final case class CounterMetric(name: String, description: String, labelsKeys: List[String])
  extends Counter with Collector with Labelable[Counter] {

  private val children = new AtomicReference[Map[LabelsValues, CounterChild]](Map.empty)

  private val noLabel = new CounterChild(List.empty)

  override def inc(): Unit = noLabel.inc()

  override def inc(value: Double): Unit = noLabel.inc(value)

  @tailrec
  override def labels(value: LabelsValues): Counter = {
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
      case (_, value) => Series(name, labelsKeys, value.labelValues, value.value)
    }(collection.breakOut)
    val voidSeries = Series(name, labelsKeys, noLabel.labelValues, noLabel.value)

    Sample(this, voidSeries :: childSeries)
  }
}

object Counter {
  def build(name: String, description: String, labelsKeys: List[String] = List.empty): CounterMetric =
    CounterMetric(name, description, labelsKeys)
}
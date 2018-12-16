package org.verdande.core

import java.util.concurrent.atomic.{AtomicReference, DoubleAdder}

import scala.annotation.tailrec

trait Gauge {
  def inc(): Unit

  def inc(value: Double): Unit

  def dec(): Unit

  def dec(value: Double): Unit

  def set(value: Double): Unit

  def setToCurrentTime()(implicit timeProvider: TimeProvider): Unit = {
    set(timeProvider.timestamp)
  }

  def time[A](f: => A)(implicit timeProvider: TimeProvider) = {
    val start = timeProvider.now
    try {
      f
    } finally {
      set(timeProvider.durationFrom(start))
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

private[core] class GaugeChild(labelsValues: LabelsValues) extends Gauge {
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

  def value = Series(labelsValues.values, incs.doubleValue() - decs.doubleValue())
}

final case class GaugeMetric(name: String,
                             description: String,
                             labelsKeys: List[String]) extends Gauge with Collector with Labelable[Gauge] {

  private val childs = new AtomicReference[Map[LabelsValues, GaugeChild]](Map.empty)

  private val noLabel = new GaugeChild(LabelsValues.empty)

  override def inc(): Unit = noLabel.inc()

  override def inc(value: Double): Unit = noLabel.inc(value)

  override def dec(): Unit = noLabel.dec()

  override def dec(value: Double): Unit = noLabel.dec(value)

  override def set(value: Double): Unit = noLabel.set(value)

  @tailrec
  override def labels(values: LabelsValues): Gauge = {
    val now = childs.get()
    now.get(values) match {
      case Some(gauge) => gauge
      case None =>
        val child = new GaugeChild(values)
        val next = now.updated(values, child)
        if (childs.compareAndSet(now, next)) {
          child
        } else {
          labels(values)
        }
    }
  }

  override def collect(): Sample = {
    val childSeries: List[Series] = childs.get().map {
      case (_, value) => value.value
    }(collection.breakOut)
    val noLabelSeries = noLabel.value

    Sample(this, noLabelSeries :: childSeries)
  }
}

object Gauge {
  def build(name: String, description: String, labelsKeys: List[String] = List.empty): GaugeMetric = {
    GaugeMetric(name, description, labelsKeys)
  }
}




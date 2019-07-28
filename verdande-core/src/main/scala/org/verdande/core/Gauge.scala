package org.verdande.core

import java.util.concurrent.atomic.{AtomicReference, DoubleAdder}

import scala.annotation.tailrec

trait Gauge {
  def inc(): Unit

  def inc(value: Double): Unit

  def dec(): Unit

  def dec(value: Double): Unit

  def set(value: Double): Unit

  /**
    * Set gauge to current time - resolution determinate by time provider
    */
  def setToCurrentTime()(implicit time: TimestampProvider): Unit =
    set(time.timestamp())

  /**
    * Track execution time of provided function - maximum resolution determinate by time provider
    */
  def time[A](f: => A)(implicit time: PeriodProvider): A = {
    val start = time.mark
    try {
      f
    } finally {
      set(time.durationFrom(start))
    }
  }

  /**
    * Track number of concurrent running type of task
    */
  def track[A](f: => A): A = {
    inc()
    try {
      f
    } finally {
      dec()
    }
  }
}

private[core] case class GaugeChild(labelsValues: LabelsValues) extends Gauge {
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

  def value = incs.doubleValue() - decs.doubleValue()
}

final case class GaugeMetric(name: String, description: String, labelsKeys: List[String]) extends Gauge with Collector with Labelable[Gauge] {

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
    val childSeries: List[Series] = childs
      .get()
      .map {
        case (_, value) => Series(name, labelsKeys, value.labelsValues.values, value.value)
      }(collection.breakOut)
    val noLabelSeries = Series(name, labelsKeys, noLabel.labelsValues.values, noLabel.value)

    Sample(this, noLabelSeries :: childSeries)
  }
}

object Gauge {
  def build(name: String, description: String, labelsKeys: List[String] = List.empty): GaugeMetric =
    GaugeMetric(name, description, labelsKeys)
}

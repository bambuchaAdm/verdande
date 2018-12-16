package org.verdande.core

import java.util.concurrent.atomic.{AtomicReference, DoubleAdder}

import scala.annotation.tailrec

trait Histogram {
  def observe(value: Double): Unit

  def time[A](f: => A)(implicit timeProvider: TimeProvider): A = {
    val now = timeProvider.now
    try {
      f
    } finally {
      observe(timeProvider.durationFrom(now))
    }
  }
}

private[core] final case class HistogramChild(labelsKeys: List[String],
                                              labelsValues: List[String],
                                              buckets: Seq[Double]) extends Histogram {
  private val labels: Seq[String] = buckets.map(Collector.asString)(collection.breakOut)

  private val adders = Array.fill(buckets.size)(new DoubleAdder)

  private val sum = new DoubleAdder()

  override def observe(value: Double): Unit = {
    var index = 0
    while (index < buckets.length && value > buckets(index)) {
      index += 1
    }
    adders(index).add(1.0)
    sum.add(value)
  }

  def series(name: String): Iterable[Series] = {
    def countSeries(count: Double) = Series(name + "_count", labelsKeys, labelsValues, count)

    def sumSeries = Series(name + "_sum", labelsKeys, labelsValues, sum.sum())

    def bucketsSeries(index: Int, accumulator: Double, outcome: List[Series]): List[Series] = {
      if (index < buckets.length) {
        val result = adders(index).sum() + accumulator
        val next = Series(name + "_bucket", "le" :: labelsKeys, labels(index) :: labelsValues, result) :: outcome
        bucketsSeries(index + 1, result, next)
      } else {
        countSeries(accumulator) :: sumSeries :: outcome
      }
    }

    bucketsSeries(0, 0.0, List.empty)
  }

}

final case class HistogramMetric(name: String,
                                 description: String,
                                 labelsKeys: List[String],
                                 buckets: Seq[Double]) extends Collector with Histogram with Labelable[Histogram] {

  private val children = new AtomicReference[Map[LabelsValues, HistogramChild]](Map.empty)

  private val noLabels = HistogramChild(labelsKeys, labelsKeys.map(_ => ""), buckets)

  override def observe(value: Double): Unit = {
    noLabels.observe(value)
  }

  override def collect(): Sample = {
    val series = children.get().flatMap {
      case (_, child) => child.series(name)
    } ++ noLabels.series(name)
    Sample(this, series)
  }

  @tailrec
  override def labels(values: LabelsValues): Histogram = {
    val now = children.get()
    now.get(values) match {
      case Some(gauge) => gauge
      case None =>
        val child = HistogramChild(labelsKeys, values.values, buckets)
        val next = now.updated(values, child)
        if (children.compareAndSet(now, next)) {
          child
        } else {
          labels(values)
        }
    }
  }
}

object Histogram {
  val defaultBuckets = Array(0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1, 2.5, 5.0, 7.5, 10, Double.PositiveInfinity)

  def build(name: String, description: String, labelsKeys: List[String] = List.empty): HistogramMetric = {
    HistogramMetric(name, description, labelsKeys, defaultBuckets)
  }

}
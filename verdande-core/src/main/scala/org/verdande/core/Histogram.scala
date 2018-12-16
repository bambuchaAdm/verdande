package org.verdande.core

trait Histogram {
  def observe(value: Double): Unit
}

final case class HistogramMetric(name: String,
                                 description: String,
                                 labelsKeys: List[String],
                                 buckets: Array[Double]) extends Collector with Labelable[Histogram] {

  override def collect(): Sample = {
    Sample(this, List(
      Series(name + "_count", Seq.empty, 0.0),
      Series(name + "_sum", Seq.empty, 0.0)
    ))
  }

  override def labels(labels: LabelsValues): Histogram = ???
}

object Histogram {
  val defaultBuckets = Array(0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1, 2.5, 5, 7.5, 10)

  def build(name: String, description: String, labelsKeys: List[String] = List.empty): HistogramMetric = {
    HistogramMetric(name, description, labelsKeys, defaultBuckets)
  }
}
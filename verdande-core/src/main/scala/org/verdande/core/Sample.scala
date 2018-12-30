package org.verdande.core

case class Series(name: String, labelKeys: List[String], labelValues: List[String], value: Double)

case class Sample(metric: Metric, series: Iterable[Series])

object Sample {
  def apply(metric: Metric, series: Series*): Sample = new Sample(metric, series.toList)
}

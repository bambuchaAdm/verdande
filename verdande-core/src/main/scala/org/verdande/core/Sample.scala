package org.verdande.core

case class Series(labels: Seq[String], value: Double)

case class Sample(metric: Metric, series: List[Series])

object Sample {
  def apply(metric: Metric, series: Series*): Sample = new Sample(metric, series.toList)
}











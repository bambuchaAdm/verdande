package org.verdande.core

case class Series(labels: Seq[String], value: Double)

case class Sample(metric: Collector, series: List[Series])

object Sample {
  def apply(metric: Collector, series: Series*): Sample = new Sample(metric, series.toList)
}











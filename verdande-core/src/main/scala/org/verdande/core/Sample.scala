package org.verdande.core

trait Series {
  def labels: Seq[String]
  def value: Double
}

case class SimpleSeries(labels: Seq[String], value: Double) extends Series

trait Sample {
  def metric: Metric
  def series: Seq[Series]
}

case class SimpleSample(metric: Metric, series: Seq[Series]) extends Sample











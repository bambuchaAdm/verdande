package org.verdande.core

case class Series(labels: Seq[String], value: Double)

case class Sample(metric: Metric, series: Seq[Series])











package org.verdande.core

trait Metric {
  def name: String

  def description: String

  def labelsKeys: List[String]
}

final case class SimpleMetric(name: String, description: String, labelsKeys: List[String]) extends Metric

trait Collector extends Metric {

  def collect(): Sample

  def register()(implicit registry: CollectorRegistry): this.type = {
    registry.register(this)
    this
  }
}









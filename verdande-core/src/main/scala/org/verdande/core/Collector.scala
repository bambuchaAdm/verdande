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

object Collector {
  def asString(value: Double): String = value match {
    case Double.PositiveInfinity => "+Inf"
    case Double.NegativeInfinity => "-Inf"
    case Double.NaN              => "NaN"
    case other                   => java.lang.Double.toString(other)
  }
}

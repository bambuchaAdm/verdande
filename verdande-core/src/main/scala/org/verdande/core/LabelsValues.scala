package org.verdande.core

case class LabelsValues(values: List[String])

object LabelsValues {
  val empty = new LabelsValues(List.empty)

  def apply(metric: Collector, labelsValue: String*): LabelsValues = {
    if(metric.labelsKeys.length != labelsValue.length){
      throw new IllegalArgumentException("Number of labels values must match to metric labels names")
    }
    LabelsValues(labelsValue.toList)
  }
  def apply(metric: Collector, labels: Map[String, String]): LabelsValues = {
    apply(metric, metric.labelsKeys.map(name => labels.getOrElse(name, "")): _*)
  }
}

trait Labelable[T] { self: Collector =>
  def labels(labels: LabelsValues): T
  def labels(values: String*): T = labels(LabelsValues.apply(this, values: _*))
  def labels(values: Map[String, String]): T = labels(LabelsValues.apply(this, values))
}
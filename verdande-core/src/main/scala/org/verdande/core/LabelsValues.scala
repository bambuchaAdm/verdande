package org.verdande.core

case class LabelsValues(values: List[String])

object LabelsValues {
  def apply(metric: Metric, labelsValue: String*): LabelsValues = {
    if(metric.labelsKeys.length != labelsValue.length){
      throw new IllegalArgumentException("Number of labels values must match to metric labels names")
    }
    LabelsValues(labelsValue.toList)
  }
  def apply(metric: Metric, labels: Map[String, String]): LabelsValues = {
    apply(metric, metric.labelsKeys.map(name => labels.getOrElse(name, "")): _*)
  }
}

trait Labelable[T] { self: Metric =>
  def labels(labels: LabelsValues): T
  def labels(values: String*): T = labels(LabelsValues.apply(this, values: _*))
  def labels(values: Map[String, String]): T = labels(LabelsValues.apply(this, values))
}
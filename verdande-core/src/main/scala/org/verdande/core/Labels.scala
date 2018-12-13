package org.verdande.core

case class Labels(values: List[String])

object Labels {
  def apply(metric: Metric, labelsValue: String*): Labels = {
    if(metric.labelsKeys.length != labelsValue.length){
      throw new IllegalArgumentException("Number of labels values must match to metric labels names")
    }
    Labels(labelsValue.toList)
  }
  def apply(metric: Metric, labels: Map[String, String]): Labels = {
    apply(metric, metric.labelsKeys.map(name => labels.getOrElse(name, "")): _*)
  }
}

trait Labelable[T] { self: Metric =>
  def labels(labels: Labels): T
  def labels(values: String*): T = labels(Labels.apply(this, values: _*))
  def labels(values: Map[String, String]): T = labels(Labels.apply(this, values))
}
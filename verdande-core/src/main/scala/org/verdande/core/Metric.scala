package org.verdande.core

trait Metric {
  def name: String
  def description: String
  def labelsKeys: Seq[String]
}

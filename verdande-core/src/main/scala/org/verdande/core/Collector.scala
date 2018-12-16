package org.verdande.core

trait Collector {
  def name: String

  def description: String

  def labelsKeys: List[String]

  def collect(): Sample

  def register()(implicit registry: CollectorRegistry): this.type = {
    registry.register(this)
    this
  }
}









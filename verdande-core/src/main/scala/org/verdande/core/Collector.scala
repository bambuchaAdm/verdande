package org.verdande.core

trait Collector {
  def collect(): Sample

  def register()(implicit registry: CollectorRegistry): this.type = {
    registry.register(this)
    this
  }
}









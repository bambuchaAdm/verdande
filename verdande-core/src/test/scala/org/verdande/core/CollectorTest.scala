package org.verdande.core

import org.scalatest.FlatSpec

class CollectorRegistry() {
  def register(collector: Collector): Any = ???

}

class Collector()

class MetricRegistryTest extends FlatSpec {

  behavior of "Collector Registry"

  it should "register collector" in {
    val registry = new CollectorRegistry()
    val collector = new Collector()
    registry.register(collector)

  }

}





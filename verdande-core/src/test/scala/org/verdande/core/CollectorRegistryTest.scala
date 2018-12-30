package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}
import org.verdande.examples.ExampleCollector

class CollectorRegistryTest extends FlatSpec with Matchers {

  behavior of "Collector Registry"

  it should "allow register collector" in {
    val registry = CollectorRegistry()
    val collector = new ExampleCollector()
    registry.register(collector)
    registry should contain(collector)
  }

  it should "allow to unregister collector" in {
    val registry = CollectorRegistry()
    val collector = new ExampleCollector()
    registry.register(collector)
    registry.unregister(collector)
    registry shouldBe empty
  }
}

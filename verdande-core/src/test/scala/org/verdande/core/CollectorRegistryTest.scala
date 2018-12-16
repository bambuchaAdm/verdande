package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class CollectorRegistryTest extends FlatSpec with Matchers {

  class ExampleCollector extends Collector with Metric {

    override def name: String = "example"

    override def description: String = "Example metric for tests"

    override def labelsKeys: List[String] = List.empty

    override def collect(): Sample = {
      Sample(this, List.empty)
    }
  }

  behavior of "Collector Registry"

  it should "allow register collector" in {
    val registry = CollectorRegistry()
    val collector = new ExampleCollector()
    registry.register(collector)
    registry should contain (collector)
  }

  it should "allow to unregister collector" in {
    val registry = CollectorRegistry()
    val collector = new ExampleCollector()
    registry.register(collector)
    registry.unregister(collector)
    registry shouldBe empty
  }
}

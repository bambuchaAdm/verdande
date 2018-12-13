package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class CollectorRegistryTest extends FlatSpec with Matchers {

  class ExampleCollector extends Collector with Metric {

    override def name: String = "example"

    override def description: String = "Example metric for tests"

    override def labelsNames: Seq[String] = Seq.empty

    override def collect(): Sample = {
      SimpleSample(this, Seq.empty)
    }
  }

  behavior of "Collector Registry"

  it should "allow register collector" in {
    val registry = CollectorRegistry()
    val collector = new ExampleCollector()
    val after = registry.register(collector)
    after should contain (collector)
  }

  it should "allow to unregister collector" in {
    val registry = CollectorRegistry()
    val collector = new ExampleCollector()
    val after = registry.register(collector)
    after.unregister(collector) shouldNot contain (collector)
  }
}

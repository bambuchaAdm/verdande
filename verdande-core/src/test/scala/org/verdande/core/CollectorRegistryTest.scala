package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class CollectorRegistryTest extends FlatSpec with Matchers {

  behavior of "Collector Registry"

  it should "allow register collector" in {
    val registry = CollectorRegistry()
    val collector = new Collector()
    val after = registry.register(collector)
    after should contain (collector)
  }

  it should "allow to unregister collector" in {
    val registry = CollectorRegistry()
    val collector = new Collector()
    val after = registry.register(collector)
    after.unregister(collector) shouldNot contain (collector)
  }
}

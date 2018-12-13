package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class CounterTest extends FlatSpec with Matchers {
  behavior of "Counter"

  it should "allow to increment by one" in {
    val counter = Counter()
    counter.inc()
    counter.value shouldEqual 1.0
  }
}

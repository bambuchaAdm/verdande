package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class CounterTest extends FlatSpec with Matchers {
  behavior of "Counter"

  it should "allow to increment by one" in {
    val counter = Counter()
    counter.inc()
    counter.value shouldEqual 1.0
  }

  it should "allow to increment by more then one" in {
    val value  = 10.0
    val counter = Counter()
    counter.inc(value)
    counter.value shouldEqual value
  }

  it should "throw exception on negative increment" in {
    val counter = Counter()
    intercept[IllegalArgumentException] {
      counter.inc(-1.0)
    }
  }
}

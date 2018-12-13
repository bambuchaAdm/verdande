package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class CounterTest extends FlatSpec with Matchers {
  behavior of "Counter"

  trait ExampleCounter {
    val counter: CounterMetric = Counter.build(
      name = "example_counter",
      description = "Example counter without registring it anywhere for tests"
    )
  }

  it should "start from zero" in new ExampleCounter {
    counter.value shouldEqual 0.0
  }

  it should "allow to increment by one" in new ExampleCounter {
    counter.inc()
    counter.value shouldEqual 1.0
  }

  it should "allow to increment by more then one" in new ExampleCounter {
    val value  = 10.0
    counter.inc(value)
    counter.value shouldEqual value
  }

  it should "throw exception on negative increment" in new ExampleCounter {
    intercept[IllegalArgumentException] {
      counter.inc(-1.0)
    }
  }
}

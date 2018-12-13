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
    val scrap = counter.collect()
    scrap.series should have length 1
    scrap.series.head.value shouldEqual 0.0
  }

  it should "allow to increment by one" in new ExampleCounter {
    val scrap = counter.collect()
    scrap.series should have length 1
    scrap.series.head.value shouldEqual 0.0
  }

  it should "allow to increment by more then one" in new ExampleCounter {
    val value  = 10.0
    counter.inc(value)
    val scrap = counter.collect()
    scrap.series should have length 1
    scrap.series.head.value shouldEqual value
  }

  it should "throw exception on negative increment" in new ExampleCounter {
    intercept[IllegalArgumentException] {
      counter.inc(-1.0)
    }
  }

  it should "allow set lables" in {
    val metric = Counter.build(
      name = "example_counter",
      description = "Example counter without registring it anywhere for tests",
      labelsKeys = Seq("foo", "bar")
    )
    val counter = metric.labels("example", "example")
    counter.inc()
    val sample = metric.collect()
    sample.series should have size 2
    sample.series.map(_.labels) should contain(Seq("example", "example"))
  }
}

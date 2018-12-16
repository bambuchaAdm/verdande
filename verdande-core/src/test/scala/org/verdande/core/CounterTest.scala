package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class CounterTest extends FlatSpec with Matchers {
  behavior of "Counter"

  trait ExampleCounter {
    val counter: CounterMetric = Counter.build(
      name = "example_counter",
      description = "Example counter without registring it anywhere for tests"
    )

    def shouldHaveOnlyOneSeries(f: Series => Unit): Unit = {
      val result = counter.collect()
      result.series should have size 1
      val series = result.series.head
      f(series)
    }
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
      labelsKeys = List("foo", "bar")
    )
    val counter = metric.labels("example", "example")
    counter.inc()
    val sample = metric.collect()
    sample.series should have size 2
    sample.series.map(_.labels) should contain(Seq("example", "example"))
  }

  it should "allow register to default registry" in {
    val metric = Counter.build(
      name = "example_counter",
      description = "Example counter without registring it anywhere for tests",
      labelsKeys = List("foo", "bar")
    ).register()
    CollectorRegistry.default should contain (metric)
  }

  it should "allow register in any other registry" in {
    val other = CollectorRegistry()
    val metric = Counter.build(
      name = "example_counter",
      description = "Example counter without registring it anywhere for tests",
      labelsKeys = List("foo", "bar")
    ).register()(other)
    other should contain (metric)
  }

  it should "count exceptions" in new ExampleCounter {
    intercept[RuntimeException] {
      counter.countExceptions() {
        throw new RuntimeException("Example exception")
      }
    }
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual 1.0
    }
  }

  it should "allow to narrow " in new ExampleCounter {
    intercept[RuntimeException] {
      counter.countExceptions(classOf[IllegalArgumentException]) {
        throw new RuntimeException("Example exception")
      }
    }
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual 0.0
    }
  }
}

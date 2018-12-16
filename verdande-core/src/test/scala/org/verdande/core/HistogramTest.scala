package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class HistogramTest extends FlatSpec with Matchers {

  behavior of "Histogram"

  trait ExampleHistogram {
    val historgram = Histogram.build(
      name = "example_histogram",
      description = "Histogram for tests"
    )
  }

  it should "produce count of observed values" in new ExampleHistogram {
    val sample = historgram.collect()
    exactly(1, sample.series) should have (
      'name ("example_histogram_count")
    )
  }

  it should "produce sum of observed values" in new ExampleHistogram {
    val sample = historgram.collect()
    exactly(1, sample.series) should have (
      'name ("example_histogram_sum")
    )
  }

  it should "produce +Inf bucket of observed values" in new ExampleHistogram {
    val sample = historgram.collect()
    exactly(1, sample.series) should have (
      'name ("example_histogram_bucket"),
      'labelValues (List("+Inf"))
    )
  }

  it should "produce default backets same as other libraries" in new ExampleHistogram {
    // Taken from java simple_client
    val buckets = Seq
    val sample = historgram.collect()
    sample.series.filter(_.name == "example_histogram_bucket").flatMap(_.labelValues) should contain allOf(
      "0.005", "0.01", "0.025", "0.05", "0.075", "0.1", "0.25", "0.5", "0.75", "1.0", "2.5", "5.0", "7.5", "10.0"
    )
  }

  it should "allow to add labels" in {
    val histogram = Histogram.build(
      name = "example_labeled_histogram",
      description = "Histogram with labels",
      labelsKeys = List("foo", "bar")
    )
    histogram.labels("fizz", "buzz")
    val sample = histogram.collect()
    atLeast(1, sample.series) should have (
      'labelKeys (List("foo", "bar")),
      'labelValues (List("fizz", "buzz"))
    )
  }


}

package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class BucketTest extends FlatSpec with Matchers {

  behavior of "Bucket Factory"

  it should "create linear bucket" in {
    Buckets.linear(start = 1, width = 1, count = 10) should contain only (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, Double.PositiveInfinity)
  }

  it should "create exponential buckets" in {
    Buckets.exponential(start = 1, factor = 2, count = 10) should contain only (1, 2, 4, 8, 16, 32, 64, 128, 256, 512, Double.PositiveInfinity)
  }

  it should "create exponential buckets for histogram" in {
    Histogram.build("linear_histogram", "Linear histogram examle", buckets = Buckets.exponential(1, 2, 10))
  }

  it should "create linear buckets for histogram" in {
    Histogram.build("linear_histogram", "Linear histogram examle", buckets = Buckets.linear(1, 2, 10))
  }

  it should "create default buckets for histogram" in {
    Histogram.build("linear_histogram", "Linear histogram examle")
  }

}

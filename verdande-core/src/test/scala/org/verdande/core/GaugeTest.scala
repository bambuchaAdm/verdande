package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

class GaugeTest extends FlatSpec with Matchers {

  behavior of "Gauge"

  trait Setup {
    val gauge = Gauge.build(
      name = "example",
      description = "Example gauge for tests"
    )
  }

  it should "allow increment by one" in new Setup {
    gauge.inc()
    val result = gauge.collect()
    result.series should have size 1
    val series = result.series.head
    series.value shouldEqual 1.0
  }
}

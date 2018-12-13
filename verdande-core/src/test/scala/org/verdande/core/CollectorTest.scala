package org.verdande.core

import org.scalatest.{FlatSpec, Matchers}

trait Sample {

}

class SampleSeries(samples: Seq[Sample]) extends Iterable[Sample] {
  override def iterator: Iterator[Sample] = samples.iterator
}

class Collector() {
  def collect(): SampleSeries = ???

}

class CollectorTest extends FlatSpec with Matchers {

  behavior of "Collector"

  it should "returns zero or more metrics and their samples" in {
    val collector = new Collector()
    val result = collector.collect()
    result shouldBe a[Iterable[Sample]]
  }

}







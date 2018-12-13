package org.verdande.core

trait Sample {

}

class SampleSeries(samples: Seq[Sample]) extends Iterable[Sample] {
  override def iterator: Iterator[Sample] = samples.iterator
}














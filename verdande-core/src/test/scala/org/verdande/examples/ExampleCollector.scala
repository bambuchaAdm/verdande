package org.verdande.examples

import org.verdande.core.{Collector, Sample}

class ExampleCollector extends Collector {

  override def name: String = "example"

  override def description: String = "Example metric for tests"

  override def labelsKeys: List[String] = List.empty

  override def collect(): Sample =
    Sample(this, List.empty)
}

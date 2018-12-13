package org.verdande.core

import scala.collection.immutable.ListSet

class CollectorRegistry(collectors: ListSet[Collector]) extends Iterable[Collector] {

  def register(collector: Collector): CollectorRegistry = {
    new CollectorRegistry(collectors + collector)
  }

  override def iterator: Iterator[Collector] = collectors.iterator
}

object CollectorRegistry {
  def apply(): CollectorRegistry = new CollectorRegistry(ListSet.empty)
}

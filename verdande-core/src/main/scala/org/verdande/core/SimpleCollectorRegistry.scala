package org.verdande.core

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.collection.immutable.ListSet

trait CollectorRegistry {
  def unregister(collector: Collector): CollectorRegistry
  def register(collector: Collector): CollectorRegistry
  def iterator: Iterator[Collector]
}

object CollectorRegistry  {
  def apply(): CollectorRegistry = new SimpleCollectorRegistry(ListSet.empty)

  implicit val default: CollectorRegistry = DefaultCollectorRegistry
}

private[core] class SimpleCollectorRegistry(collectors: ListSet[Collector]) extends CollectorRegistry {

  def unregister(collector: Collector): SimpleCollectorRegistry = {
    new SimpleCollectorRegistry(collectors - collector)
  }

  def register(collector: Collector): SimpleCollectorRegistry = {
    new SimpleCollectorRegistry(collectors + collector)
  }

  override def iterator: Iterator[Collector] = collectors.iterator
}

private[core] object DefaultCollectorRegistry extends CollectorRegistry {
  private val ref = new AtomicReference[SimpleCollectorRegistry](new SimpleCollectorRegistry(ListSet.empty))

  @tailrec
  override def unregister(collector: Collector): CollectorRegistry = {
    val prev = ref.get()
    val next = prev.unregister(collector)
    if(ref.compareAndSet(prev, next)) {
      this
    } else {
      unregister(collector)
    }
  }

  override def register(collector: Collector): CollectorRegistry = {
    val prev = ref.get()
    val next = prev.register(collector)
    if(ref.compareAndSet(prev, next)) {
      this
    } else {
      register(collector)
    }
  }

  override def iterator: Iterator[Collector] = ref.get().iterator
}

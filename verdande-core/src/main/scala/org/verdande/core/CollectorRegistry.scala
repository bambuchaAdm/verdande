package org.verdande.core

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.collection.immutable.ListSet

trait CollectorRegistry extends Iterable[Collector] {
  def unregister(collector: Collector): Collector
  def register(collector: Collector): Collector
  def iterator: Iterator[Collector]
}

object CollectorRegistry  {
  def apply(): CollectorRegistry = new DefaultCollectorRegistry

  implicit val default: CollectorRegistry = new DefaultCollectorRegistry
}

private[core] class ImmutableRegistry(collectors: ListSet[Collector]) extends Iterable[Collector] {

  def unregister(collector: Collector): ImmutableRegistry = {
    new ImmutableRegistry(collectors - collector)
  }

  def register(collector: Collector): ImmutableRegistry = {
    new ImmutableRegistry(collectors + collector)
  }

  override def iterator: Iterator[Collector] = collectors.iterator
}

private[core]final class DefaultCollectorRegistry extends CollectorRegistry {
  private val ref = new AtomicReference[ImmutableRegistry](new ImmutableRegistry(ListSet.empty))

  @tailrec
  override def unregister(collector: Collector): Collector = {
    val prev = ref.get()
    val next = prev.unregister(collector)
    if(ref.compareAndSet(prev, next)) {
      collector
    } else {
      unregister(collector)
    }
  }

  override def register(collector: Collector): Collector = {
    val prev = ref.get()
    val next = prev.register(collector)
    if(ref.compareAndSet(prev, next)) {
      collector
    } else {
      register(collector)
    }
  }

  override def iterator: Iterator[Collector] = ref.get().iterator
}

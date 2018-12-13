package org.verdande.core

trait Gauge {
  def inc(): Unit
  def inc(value: Double): Unit
  def dec(): Unit
  def dec(value: Double): Unit
  def set(value: Double): Unit
}


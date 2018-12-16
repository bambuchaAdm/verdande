package org.verdande.core

object Buckets {
  def linear(start: Double, width: Double, count: Int): Array[Double] = {
    Iterator.iterate(start)(prev => prev + width).take(count).to[Array] :+ Double.PositiveInfinity
  }

  def exponential(start: Double, factor: Double, count: Int): Array[Double] = {
    Iterator.iterate(start)(prev => prev * factor).take(count).to[Array] :+ Double.PositiveInfinity
  }
}

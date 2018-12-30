package org.verdande.core

import java.time.Instant
import java.util.concurrent.CountDownLatch

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import org.verdande.core

import scala.concurrent.{ExecutionContext, Future}

class GaugeTest extends FlatSpec with Matchers with ScalaFutures {

  behavior of "Gauge"

  trait Setup {
    val gauge = Gauge.build(name = "example", description = "Example gauge for tests")

    def shouldHaveOnlyOneSeries(f: Series => Unit): Unit = {
      val result = gauge.collect()
      result.series should have size 1
      val series = result.series.head
      f(series)
    }

  }

  it should "start form 0" in new Setup {
    val result = gauge.collect()
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual 0.0
    }
  }

  it should "allow increment by one" in new Setup {
    gauge.inc()
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual 1.0
    }
  }

  it should "allow increment by arbitrary value" in new Setup {
    private val step = 10.0
    gauge.inc(step)
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual step
    }
  }

  it should "allow decrement by one" in new Setup {
    gauge.dec()
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual -1.0
    }
  }

  it should "allow decrement by arbitrary value" in new Setup {
    private val step = 10.0
    gauge.dec(step)
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual -10.0
    }
  }

  it should "allow set to arbitrary value" in new Setup {
    gauge.inc(1.0)
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual 1.0
    }
    gauge.set(42.0)
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual 42.0
    }
  }

  it should "allow set to current unix timestamp" in new Setup {
    val start = Instant.now().getEpochSecond.toDouble
    gauge.setToCurrentTime()
    val end = Instant.now().getEpochSecond.toDouble
    shouldHaveOnlyOneSeries { series =>
      series.value should (be >= start and be <= end)
    }
  }

  it should "allow measure how long is task running" in new Setup {
    gauge.time(Thread.sleep(100))
    shouldHaveOnlyOneSeries { series =>
      series.value should (be >= 0.1 and be <= 0.125) // 5ms margin for threads FIXME use external mockable time provider
    }
  }

  it should "track number of in-progress calls" in new Setup {
    implicit val ec = ExecutionContext.global
    private val maxRequests = 10
    val preparationLatch = new CountDownLatch(maxRequests)
    val requests = Range(0, maxRequests).map { _ =>
      val latch = new CountDownLatch(1)
      val future = Future.apply {
        gauge.track {
          preparationLatch.countDown()
          latch.await()
        }
      }
      (latch, future)
    }
    preparationLatch.await()
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual maxRequests
    }
    requests.foreach {
      case (latch, future) =>
        latch.countDown()
        future.futureValue
        shouldHaveOnlyOneSeries { series =>
          series.value shouldEqual requests.filterNot(_._2.isCompleted).length
        }
    }
  }

  it should "handle exceptions when tracking requests" in new Setup {
    intercept[RuntimeException] {
      gauge.track(throw new RuntimeException("example"))
    }
    shouldHaveOnlyOneSeries { series =>
      series.value shouldEqual 0.0
    }
  }

  it should "support labels" in {
    val gauge = Gauge.build(name = "example_gauge_with_labels", description = "Example gauge for label tests", labelsKeys = List("foo", "bar"))
    gauge.labels("fizz", "buzz")
    val sample = gauge.collect()
    sample.series should have size 2
    sample.series.map(_.labelValues) should contain(Seq("fizz", "buzz"))
  }
}

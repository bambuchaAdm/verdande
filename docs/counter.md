# Counter

Counter is monotonically growing only type of metric. Best for measure:
* number of request 
* number of exception
* number of misses in cache usage

## Example usage


```scala mdoc:silent
import org.verdande.core.Counter

class SomeService {
  import SomeService._
  def foo() = {
    total.inc()
    error.countExceptions(){
        // Code here
    }
  }
}

object SomeService {
  val total = Counter.build(
    name = "foo.total",
    description = "total number of foo usage"
  ).register()
  
  val error = Counter.build(
    name = "foo.error",
    description = "total number of error in foo"  
  ).register() 
}

```

## Labels

Prometheus allow add labels for metric. It very useful feature for having
single metric with multiple meaning. 

Good example are http response status codes - 200, 303 etc.

Implementation to measure counter of different statuses from http.

To obtain counter with specific set of labels, `lables` method should be used.
Methods have two variants - one which take sequence of label values in same order as was defined in Counter construction.
Second take map from label name to label value and internally create sequence with correct order.

### Example


```scala mdoc:silent
import org.verdande.core.Counter

class SomeConnetor2 {
  import SomeConnector2._
  
  def makeRequest(): { def code: Int } = ???
  
  def foo() = {
    val response = makeRequest()
    responses.labels(response.code.toString).inc()
  }
}

object SomeConnector2 {
  val responses = Counter.build(
    name = "http.response",
    description = "Total number of response in HTTP comunication",
    labelsKeys = List("code")
  ).register()
}
```

### Labeled counter caching

For host paths calling for child counter could be expensive operation.
Child counter could be assigned to local variable for omit lookup in child collection.

```scala mdoc:silent
import org.verdande.core.Counter

class SomeConnetor3(family: String) {
  import SomeConnector3._
  
  private val localResponses = responses.labels(family)
  
  def makeRequest() = ??? 
  
  def foo() = {
    Range(1,10).foreach { _ =>  
      val response = makeRequest()
      localResponses.inc()
    }
  }
}

object SomeConnector3 {
  val responses = Counter.build(
    name = "http.response",
    description = "Total number of response in HTTP comunication",
    labelsKeys = List("family")
  ).register()
}
```  


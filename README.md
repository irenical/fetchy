[![][maven img]][maven]
[![][travis img]][travis]
[![][codecov img]][codecov]

# Fetchy
Service discovery API and libraries for Java.  
Interacting with services over network is a common but less than simple endeavour. It usually requires addressing several concerns, namely service discovery, load balancing, fallback on error, timeout handling, auto-retry, circuit breaking, metrics and some kind of protocol-level handling. Fetchy aims at solving from most to all of these, while keeping a simple API, being easy to customize or extend and fully protocol agnostic.

![alt text][dog]  
Fetchy, the dog

## Usage
A trivial service call.
```java
String outcome = fetchy.call("my-service", MyServiceStub.class, MyServiceStub::getSomething);
```
or if you have to do something more with the service endpoint.
```java
String outcome = fetchy.call("my-service", MyServiceStub.class, serviceApi -> {
    String gotThis = serviceApi.getSomething();
    return gotThis == null ? serviceApi.getSomethingElse() : gotThis;
  });
```
## Concepts
Although Fetchy's API design aims at simplicity, in order to effectivelly use it as full-featured protocol-agnostic RPC framework some concepts and their naming must be first clarified.  
- Discoverer: a component responsible of finding nodes currently running a specific service
- Node: a representation of an address where a service instance was found running
- Balancer: a many-to-one node list reducer, with an arbitrary heuristic.
- Connector: a component able to return a service stub instance for a given Node
- Request: A prepared service call, bound to a specific service, node and stub

## Setup

### Registering a service
Before calling a service, it must first be registrered in a Fetchy instance. A service is identified by a String and is registered with a Connector at least.  

```java
Fetchy fetchy = new Fetchy();
fetchy.start();
fetchy.register("my-service", myDiscoverer, myBalancer, myConnector);
```
Altough this project aims to provide several implementations of Connectors, Balancers and Discoverers, you can easily implement your own, using or not Java 8's lambdas. Bellow is an example using Consul for service discovery, a trivial random balancer and a gRPC connector.
```java
Fetchy fetchy = new Fetchy();
fetchy.start();
fetchy.register("my-leet-microservice",
    new ConsulDiscoverer(),
    new RandomBalancer(),
    new GrpcConnector(channel->MyLeetMicroserviceGrpc::newBlockingStub));
```
You can then call this service like so
```java
String outcome = fetchy.call("my-leet-microservice",
    MyLeetMicroserviceBlockingStub.class,
    MyLeetMicroserviceBlockingStub::getSomething);
```

## How does it work
TODO: flow diagram

## Timeout and fallback behaviours

## Metrics

## Dependencies
You'll allways need to include the fetchy's API dependency.
```xml
<dependency>
  <groupId>org.irenical.fetchy</groupId>
  <artifactId>fetchy-api</artifactId>
  <version>2.0.0</version>
</dependency>
```
Then, either use fetchy's discoverers, balancers and connectors or create your own.


[dog]:https://www.irenical.org/fetchy/dog.jpg "Here you go. Three green cubes."

[maven]:http://search.maven.org/#search|gav|1|g:"org.irenical.fetchy"%20AND%20a:"fetchy-api"
[maven img]:https://maven-badges.herokuapp.com/maven-central/org.irenical.fetchy/fetchy-api/badge.svg

[travis]:https://travis-ci.org/irenical/fetchy
[travis img]:https://travis-ci.org/irenical/fetchy.svg?branch=master

[codecov]:https://codecov.io/gh/irenical/fetchy
[codecov img]:https://codecov.io/gh/irenical/fetchy/branch/master/graph/badge.svg

[![][maven img]][maven]
[![][travis img]][travis]
[![][codecov img]][codecov]

# Fetchy
Service discovery API and libraries for Java

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

## Setup
### Registering a service
### Balancing service nodes

## How does it work

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

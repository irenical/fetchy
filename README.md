[![][maven img]][maven]
[![][travis img]][travis]
[![][codecov img]][codecov]

# Fetchy
Service discovery API and libraries for Java

![alt text][dog]  
Fetchy, the dog

Registering a factory:
```java
Fetchy fetchy = new Fetchy();

ThriftServiceDiscoveryFactory< < THRIFT_CONTRACT >.Iface, < THRIFT_CONTRACT >.Client> factory =
  new ThriftServiceDiscoveryFactory<>( "myThriftService", < THRIFT_CONTRACT >.Iface.class, < THRIFT_CONTRACT >.Client.class, "serviceId" );

fetchy.register( factory );
```

Each factory should define its service node discovery and service node balancer:

```java
factory.setServiceNodeDiscovery( serviceNodeDiscovery );
factory.setServiceNodeBalancer( serviceNodeBalancer );
```

Note that the factory, the service discovery and the balancer can all be registered automatically with Java's ServiceLoader.


Usage example:
```java
fetchy.find( "myThriftService" ).ifPresent( stub -> {
  try {
    < RESULT > result = stub.call(
        iface -> iface.<METHOD>( < METHOD_PARAMETERS > ) );
    // ...
  } catch (Exception e) {
    e.printStackTrace();
  }
});

```

To use Fetchy you need both the API and factories. ServiceNodeBalancer and ServiceNodeDiscovery depends on your factory needs.

```xml
<dependency>
  <groupId>org.irenical.fetchy</groupId>
  <artifactId>fetchy-api</artifactId>
  <version>0.1.5</version>
</dependency>

<!-- thrift client implementation -->
<dependency>
  <groupId>org.irenical.fetchy</groupId>
  <artifactId>fetchy-thrift</artifactId>
  <version>0.1.5</version>
</dependency>
<!-- for a consul service discovery implementation -->
<dependency>
  <groupId>org.irenical.fetchy</groupId>
  <artifactId>fetchy-consul</artifactId>
  <version>0.1.5</version>
</dependency>
<!-- for a random service balancer implementation -->
<dependency>
  <groupId>org.irenical.fetchy</groupId>
  <artifactId>fetchy-random</artifactId>
  <version>0.1.5</version>
</dependency>
```

[dog]:https://www.irenical.org/fetchy/dog.jpg "Here you go. Three green cubes."

[maven]:http://search.maven.org/#search|gav|1|g:"org.irenical.fetchy"%20AND%20a:"fetchy-api"
[maven img]:https://maven-badges.herokuapp.com/maven-central/org.irenical.fetchy/fetchy-api/badge.svg

[travis]:https://travis-ci.org/irenical/fetchy
[travis img]:https://travis-ci.org/irenical/fetchy.svg?branch=master

[codecov]:https://codecov.io/gh/irenical/fetcht
[codecov img]:https://codecov.io/gh/irenical/fetchy/branch/master/graph/badge.svg

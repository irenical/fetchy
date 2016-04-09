
Fetchy is a service discovery API and libraries for Java


Registering a factory:
```java
ServiceController serviceController = new ServiceController();

ThriftServiceDiscoveryFactory< <ThriftContract>.Iface, <ThriftContract>.Client> factory =
  new ThriftServiceDiscoveryFactory<>( <ThriftContract>.Iface.class, <ThriftContract>.Client.class, "serviceId" );

serviceController.register( factory );
```

if ServiceNodeBalancer or ServiceNodeDiscovery is not registered with Java's ServiceLoader:

```java
factory.setServiceNodeDiscovery( serviceNodeDiscovery );
factory.setServiceNodeBalancer( serviceNodeBalancer );
```

Note that the factory, the service discovery and the balancer can all be registered automatically with Java's ServiceLoader.


Usage example:
```java
serviceController.find( <ThriftContract>.Iface.class ).ifPresent(serviceExecutor -> {
  try {
    < RESULT > result = serviceExecutor.execute(
        iface -> iface.< METHOD >( < METHOD_PARAMETERS > ) );
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
  <version>0.0.1-SNAPSHOT</version>
</dependency>

<dependency>
  <groupId>org.irenical.fetchy</groupId>
  <artifactId>fetchy-thrift</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.irenical.fetchy</groupId>
  <artifactId>fetchy-consul</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

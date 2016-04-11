
Fetchy is a service discovery API and libraries for Java


Registering a factory:
```java
ServiceController serviceController = new ServiceController();

ThriftServiceDiscoveryFactory< < THRIFT_CONTRACT >.Iface, < THRIFT_CONTRACT >.Client> factory =
  new ThriftServiceDiscoveryFactory<>( < THRIFT_CONTRACT >.Iface.class, < THRIFT_CONTRACT >.Client.class, "serviceId" );

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
serviceController.find( < THRIFT_CONTRACT >.Iface.class ).ifPresent(serviceExecutor -> {
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
  <version>0.0.1</version>
</dependency>

<dependency>
  <groupId>org.irenical.fetchy</groupId>
  <artifactId>fetchy-thrift</artifactId>
  <version>0.0.1</version>
</dependency>
<dependency>
  <groupId>org.irenical.fetchy</groupId>
  <artifactId>fetchy-consul</artifactId>
  <version>0.0.1</version>
</dependency>
```

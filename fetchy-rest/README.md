# Fetchy REST service factory implementation

assumes Retrofit2 + Moshi + RxJava adapter for retrofit

Retrofit2 : https://github.com/square/retrofit

Moshi : https://github.com/square/moshi/


Example defining a REST api for retrofit + rxjava:

```java
public interface MyApi {

    @GET( "api/{path}" )
    Observable< Response< ReturnObject > > getObject( @Path( "path" ) String path );
    
}
``` 

You can then call your api method:

```java
Fetchy.find( MyApi.class )
    .ifPresent( stub -> {
        stub.call( client -> {
            client.getObject( "some-path-value" )
                .subscribe( new Subscriber< Response< ReturnObject > >() {
                    @Override
                    public void onCompleted() {
    
                    }
    
                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }
    
                    @Override
                    public void onNext(Response< ReturnObject > response) {
                        System.out.println( "isSuccess: " + response.isSuccessful() );
                        ReturnObject object = response.body();
                        // ...
                    }
                } );
        } );
    } );
```


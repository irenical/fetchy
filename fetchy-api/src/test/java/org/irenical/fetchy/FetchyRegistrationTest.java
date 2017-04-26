package org.irenical.fetchy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FetchyRegistrationTest {

	private String serviceId = "myTestService";
	
	private String output = "Hello World!";

	private Fetchy fetchy = new Fetchy();
	
	private class MyApi {
		
		String getSomething() {
			return output;
		}
		
		void doSomething() {
		}
		
	}

	// Service
	@Before
	public void register() {
		Connector<MyApi> con = uri -> new MyApi();
		Discoverer disco = id -> null;
		Balancer bal = urls -> null;
		fetchy.register(serviceId, disco, bal, con);
	}
	
	@Test
	public void getSomethingSimple(){
		String got = fetchy.callReturning(serviceId, (Call<String,MyApi,?>) api-> api.getSomething());
		Assert.assertEquals(output, got);
	}
	
	@Test
	public void doSomethingSimple(){
		String got = fetchy.callNonReturning(serviceId, (Run<String,MyApi,?>) api-> api.doSomething());
		Assert.assertEquals(output, got);
	}
	
	@Test
	public void getSomethingWithBuilder(){
		CallBuilder<MyApi> callBuilder = fetchy.createCall(serviceId);
		callBuilder.returning(api-> api.getSomething());
		String got = callBuilder.call();
		Assert.assertEquals(output, got);
	}
	
	@Test
	public void doSomethingWithBuilder(){
		CallBuilder<MyApi> callBuilder = fetchy.createCall(serviceId);
		callBuilder.nonreturning(api-> api.doSomething());
		String got = callBuilder.call();
		Assert.assertEquals(null, got);
	}
	
	@Test(expected=IllegalStateException.class)
	public void nothingToDo(){
		CallBuilder<MyApi> callBuilder = fetchy.createCall(serviceId);
		callBuilder.call();
	}

}

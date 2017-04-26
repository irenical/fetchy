package org.irenical.fetchy;

import java.net.URI;
import java.util.function.Supplier;

public class CallBuilder<API> {

	private String serviceId;

	private URI node;
	
	private API stub;

	private Call<?, API, ?> lambdaCall;
	
	private Supplier<?> callFallback;

	private Run<?, API, ?> lambdaRun;
	
	private Runnable runFallback;
	
	private long timeoutMillis;

	public CallBuilder() {
	}

	public CallBuilder<API> service(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}

	public CallBuilder<API> node(URI node) {
		this.node = node;
		return this;
	}

	public CallBuilder<API> stub(API stub) {
		this.stub = stub;
		return this;
	}
	
	public CallBuilder<API> returning(Call<?, API, ?> lambda) {
		return returning(lambda, null);
	}

	public CallBuilder<API> returning(Call<?, API, ?> lambda, Supplier<?> fallback) {
		this.lambdaCall = lambda;
		this.callFallback = fallback;
		this.lambdaRun = null;
		this.runFallback = null;
		return this;
	}
	
	public CallBuilder<API> nonreturning(Run<?, API, ?> lambda) {
		return nonreturning(lambda, null);
	}

	public CallBuilder<API> nonreturning(Run<?, API, ?> lambda, Runnable fallback) {
		this.lambdaRun = lambda;
		this.runFallback = fallback;
		this.lambdaCall = null;
		this.callFallback = null;
		return this;
	}
	
	public CallBuilder<API> withTimeout(long millis) {
		this.timeoutMillis = millis;
		return this;
	}

	public <OUTPUT, ERROR extends Exception> OUTPUT call() throws ERROR {
		@SuppressWarnings("unchecked")
		Call<OUTPUT, API, ERROR> lc = (Call<OUTPUT, API, ERROR>) lambdaCall;
		if (lc != null) {
			return lc.call(stub);
		}
		@SuppressWarnings("unchecked")
		Run<OUTPUT, API, ERROR> lr = (Run<OUTPUT, API, ERROR>) lambdaRun;
		if (lr != null) {
			lr.run(stub);
			return null;
		}
		throw new IllegalStateException("No lambda function declared for this call");
	}
	
}

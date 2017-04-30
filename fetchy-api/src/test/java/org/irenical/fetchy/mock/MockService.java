package org.irenical.fetchy.mock;

public class MockService {
	
	private String something;
	
	public boolean ran = false;

	public MockService(String something) {
		this.something = something;
	}

	public String getSomething() {
		return something;
	}
	
	public String getSomethingWrong() throws SomethingWrongException {
		throw new SomethingWrongException();
	}

	public void doSomething() {
		ran = true;
	}
	
	public void doSomethingWrong() throws SomethingWrongException {
		throw new SomethingWrongException();
	}
	
}

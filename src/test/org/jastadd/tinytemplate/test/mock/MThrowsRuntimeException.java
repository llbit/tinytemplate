package org.jastadd.tinytemplate.test.mock;

@SuppressWarnings("javadoc")
public class MThrowsRuntimeException {
	public void m() {
		throw new RuntimeException(getClass().getName() + ".m()");
	}
}

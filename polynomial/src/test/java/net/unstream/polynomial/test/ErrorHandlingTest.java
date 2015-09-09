package net.unstream.polynomial.test;

import org.junit.Test;

public class ErrorHandlingTest extends BaseParserTest {

	@Test
	public void testErrorWrongChars() {
		assertErrorMsgStartsWith("xxxc", "token recognition error");
	}

}

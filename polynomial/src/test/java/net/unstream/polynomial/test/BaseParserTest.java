package net.unstream.polynomial.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.unstream.polynomial.PolynomialFunction;
import net.unstream.polynomial.PolynomialParseException;

public class BaseParserTest {

	public BaseParserTest() {
		super();
	}
	protected void assertErrorMsgStartsWith(final String p, final String expected) {
		try {
			new PolynomialFunction(p);
			fail("PolynomialParseException expected.");
		} catch (PolynomialParseException e) {
			assertNotNull(e);
			assertTrue("Expected message to start with '" + expected
					+ "', but got '" + e.getMessage() + "'", e.getMessage()
					.contains(expected));
		}
	}

}
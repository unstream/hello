package net.unstream.polynomial.test;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import net.unstream.polynomial.PolynomialFunction;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;

public class PolynomialTest extends BaseParserTest {
	@Test
	public void test() {
		String polynomial = "4+ c + (45.5 + 2i) - 5.7c^2 -1.5 c^1 + (4-3.5i) c^3";
		PolynomialFunction f = new PolynomialFunction(polynomial);

		Map<Integer, Complex> map = f.getCoefficientMap();
		assertComplexEquals(new Complex(49.5, 2), map.get(new Integer(0)));
		assertComplexEquals(new Complex(-0.5), map.get(new Integer(1)));
		assertComplexEquals(new Complex(-5.7), map.get(new Integer(2)));
		assertComplexEquals(new Complex(4, -3.5), map.get(new Integer(3)));
	}

	private void assertComplexEquals(Complex c1, Complex c2) {
		assertTrue(c1 + ", " + c2 + " are not equal.", Complex.equals(c1, c2));
	}


}

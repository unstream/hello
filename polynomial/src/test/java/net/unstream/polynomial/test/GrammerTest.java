package net.unstream.polynomial.test;

import static org.junit.Assert.assertEquals;
import net.unstream.polynomial.PolynomialErrorListener;
import net.unstream.polynomial.PolynomialLexer;
import net.unstream.polynomial.PolynomialParser;
import net.unstream.polynomial.PolynomialParser.TermContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.math3.complex.Complex;
import org.junit.Test;

public class GrammerTest extends BaseParserTest {

	@Test
	public void testTerm0() {
		String string = "c";
		PolynomialParser parser = buildParser(string);
		TermContext term = parser.term();
		assertEquals(1, term.exp);
		assertEquals(new Complex(1, 0), term.coeff);
	}

	@Test
	public void testTerm1() {
		String string = "3";
		PolynomialParser parser = buildParser(string);
		TermContext term = parser.term();

		assertEquals(0, term.exp);
		assertEquals(new Complex(3, 0), term.coeff);
	}

	@Test
	public void testTerm2() {
		String string = "(3 + 5i) c^5";
		PolynomialParser parser = buildParser(string);
		TermContext term = parser.term();

		assertEquals(5, term.exp);
		assertEquals(new Complex(3, 5), term.coeff);
	}

	@Test
	public void testTerm3() {
		String string = "6i c^3";
		PolynomialParser parser = buildParser(string);
		TermContext term = parser.term();

		assertEquals(3, term.exp);
		assertEquals(new Complex(0, 6), term.coeff);
	}

	
	@Test
	public void testPower1() {
		String string = "c^6";
		PolynomialParser parser = buildParser(string);
		assertEquals(6, parser.power().value);
	}

	@Test
	public void testPower2() {
		String string = "c";
		PolynomialParser parser = buildParser(string);
		assertEquals(1, parser.power().value);
	}

	@Test
	public void testComplex() {
		String string = "(45.5 + 7i)";
		PolynomialParser parser = buildParser(string);
		Complex c = parser.complex().value;

		assertEquals(45.5, c.getReal(), 0.000001);
		assertEquals(7, c.getImaginary(), 0.000001);
	}

	private PolynomialParser buildParser(String string) {
		ANTLRInputStream input = new ANTLRInputStream(string);
		PolynomialLexer lexer = new PolynomialLexer(input);
		lexer.addErrorListener(new PolynomialErrorListener());
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PolynomialParser parser = new PolynomialParser(tokens);
		return parser;
	}

}

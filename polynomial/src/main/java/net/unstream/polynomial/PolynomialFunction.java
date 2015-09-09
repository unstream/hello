package net.unstream.polynomial;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.math3.complex.Complex;

public class PolynomialFunction implements Function<Complex, Double> {
	private int maxIterations = 0;
	private Map<Integer, Complex> coefficientMap = new TreeMap<Integer, Complex>();
	private PolynomialParser parser;

	public PolynomialFunction(final String polynomial) throws PolynomialParseException {
		this(100, polynomial);
	}
	
	public PolynomialFunction(final int maxIterations, final String polynomial) throws PolynomialParseException {
		this.maxIterations = maxIterations;
		this.coefficientMap = parse(polynomial);
	}

	public Map<Integer, Complex> getCoefficientMap() {
		return coefficientMap;
	}

	@Override
	public Double apply(final Complex z) {
		int i = 0;
		double x2, y2;
		Complex z_n = z;
		Complex z_n1 = z;//Complex.ZERO; // = 
		do {
			z_n = z_n1;
			z_n1 = Complex.ZERO;
			for(Map.Entry<Integer, Complex> entry : parser.map.entrySet()) {
				z_n1 = z_n1.add(z_n.pow(entry.getKey()).multiply(entry.getValue())); 
			}
			x2 = z_n.getReal() * z_n.getReal();
			y2 = z_n.getImaginary() * z_n.getImaginary();
			i++;
		} while (i < maxIterations && (x2 + y2) < 4);//
		double nsmooth;
		if (i == maxIterations) {
			nsmooth = i;
		} else {
			nsmooth = 1d + i - Math.log(Math.log(z_n1.abs()))/Math.log(2);
		}
		return nsmooth;
	}
	
	private Map<Integer, Complex> parse(final String function) throws PolynomialParseException {
		ANTLRInputStream input = new ANTLRInputStream(function);
		PolynomialLexer lexer = new PolynomialLexer(input);
		lexer.removeErrorListeners();
		lexer.addErrorListener(new PolynomialErrorListener());
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		this.parser = new PolynomialParser(tokens);
		parser.setErrorHandler(new BailErrorStrategy());
		parser.removeErrorListeners();
		parser.addErrorListener(new PolynomialErrorListener());
		parser.polynomial();
		return parser.map;
	}
}

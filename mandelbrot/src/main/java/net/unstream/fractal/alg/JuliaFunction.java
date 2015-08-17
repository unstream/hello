package net.unstream.fractal.alg;

import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;

public class JuliaFunction implements Function<Complex, Double> {
	final private int maxIterations;
	final private Complex c;
	public JuliaFunction(final int maxIterations, final Complex c) {
		this.maxIterations = maxIterations;
		this.c = c;
	}
	
	@Override
	public Double apply(Complex z) {
		int i = 0;
		double x = z.getReal();
		double y = z.getImaginary();
		double x2, y2;
		do {
			x2 = x * x;
			y2 = y * y;
			y = 2 * x * y + c.getImaginary();
			x = x2 - y2 + c.getReal();
			i++;
		} while (i < maxIterations && (x2 + y2) < 4);
		double nsmooth;
		if (i == maxIterations) {
			nsmooth = i;
		} else {
			nsmooth = 1d + i - Math.log(Math.log(Math.sqrt(x*x + y*y)))/Math.log(2);
		}
		return nsmooth;
	}
}

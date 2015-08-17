package net.unstream.fractal.alg;

import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;

public class MandelbrotFunction implements Function<Complex, Double>  {
	final private int maxIterations;
	
	public MandelbrotFunction(final int maxIterations) {
		this.maxIterations = maxIterations;
	}
	
	@Override
	public Double apply(Complex z) {
		int i = 0;
		double x = 0;
		double y = 0;
		double x2, y2;
		do {
			x2 = x * x;
			y2 = y * y;
			y = 2 * x * y + z.getImaginary();
			x = x2 - y2 + z.getReal();
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

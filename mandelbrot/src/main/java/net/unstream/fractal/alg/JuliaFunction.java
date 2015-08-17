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

	
//	@Override
	public Double apply2(Complex z) {
        for (int i = 0; i < maxIterations; i++) {
        	
//			x2 = x * x;
//			y2 = y * y;
//			y = 2 * x * y + z.getImaginary();
//			x = x2 - y2 + z.getReal();
//			z^2 + c	= (zr+zi i) (zr+zi i) + c
//        			= zr^2 - zi^2 + cr + (2 zr zi + ci) i 
        	
            if (z.abs() > 2.0) {
            	double nsmooth = 1d + i - Math.log(Math.log(z.abs()))/Math.log(2);
            	return nsmooth;
            }
            z = z.multiply(z).add(c);
        }
        return new Double(maxIterations - 1);
	}
}

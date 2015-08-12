package net.unstream.mandelbrot.alg;

import javax.inject.Named;

import org.apache.commons.math3.complex.Complex;

public class IterateFunctions {
	
	@Named(value="fJulia")
	public IterateFunctionInterface fJulia = (maxIterations, params) -> {
		if (params.length < 2) {
			throw new IllegalArgumentException("The Julia function needs the two parameters z and c.");
		}
		Complex z = params[0]; 
		Complex c = params[1]; 
        for (int i = 0; i < maxIterations; i++) {
            if (z.abs() > 2.0) {
            	double nsmooth = 1d + i - Math.log(Math.log(z.abs()))/Math.log(2);
            	return nsmooth;
            }
            z = z.multiply(z).add(c);
        }
        return maxIterations - 1;
	};
	
	@Named(value="fMandelbrot")
	public IterateFunctionInterface fMandelbrot = (maxIterations, params) -> {
		if (params.length < 1) {
			throw new IllegalArgumentException("The Mandelbrot function needs the two parameters z.");
		}
		Complex z = params[0]; 

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
	};
}

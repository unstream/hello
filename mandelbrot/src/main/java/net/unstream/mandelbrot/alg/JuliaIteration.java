package net.unstream.mandelbrot.alg;

import org.apache.commons.math3.complex.Complex;

public class JuliaIteration {
	public static double iterate(Complex z, final Complex c, final int maxIterations) {
        for (int i = 0; i < maxIterations; i++) {
            if (z.abs() > 2.0) {
//            	return i;
            	double nsmooth = 1d + i - Math.log(Math.log(z.abs()))/Math.log(2);
            	return nsmooth;
            }
            z = z.multiply(z).add(c);
        }
        return maxIterations - 1;
    }


}

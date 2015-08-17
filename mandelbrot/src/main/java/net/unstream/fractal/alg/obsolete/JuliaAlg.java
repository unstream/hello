package net.unstream.fractal.alg.obsolete;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import net.unstream.fractal.api.domain.Fractal;

import org.apache.commons.math3.complex.Complex;

@Named("julia")
public class JuliaAlg {
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.MandelBrotAlg#compute(net.unstream.fractal.Fractal, int)
	 */
	public static Map<Integer, double[]> compute(final Fractal fractal, final int width) {
		final Complex c = new Complex( fractal.getcJulia(), fractal.getCiJulia());

		final Complex c0 = new Complex(fractal.getC0(), fractal.getC0i());
		final Complex c1 = new Complex(fractal.getC1(), fractal.getC1i());

        final double xStep = c1.subtract(c0).getReal() / width;
        final double yStep = c1.subtract(c0).getImaginary() / width;
                 
        Map<Integer, double[]> lines = new HashMap<Integer, double[]> ();
        for (int y = 0; y < width; y++) {
        	lines.put(y, new double[width]);
        }
        lines.keySet().parallelStream().forEach((y) -> {
        	double ic = yStep * y;
        	for (int x = 0; x < width; x++) {   
				Complex z = c0.add(new Complex(xStep * x, ic));
				int[] iterations = fractal.getColors().getIterations();
				double nsmooth = iterate(z, c, iterations[iterations.length - 1]);
				lines.get(y)[x] = nsmooth;
        	}        	
        });
		return lines;
	}

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

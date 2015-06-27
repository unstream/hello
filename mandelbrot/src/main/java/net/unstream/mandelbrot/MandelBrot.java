package net.unstream.mandelbrot;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.complex.Complex;

public class MandelBrot {
	
	private MandelBrot() {
	}

	public static Map<Integer, double[]> compute(final Fractal fractal, final int width) {
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
				Complex c = c0.add(new Complex(xStep * x, ic));
				double nsmooth = iterate(c, fractal.getIterations());
				lines.get(y)[x] = nsmooth;
        	}        	
        });
		return lines;
	}

	private static double iterate(final Complex c, final int maxIterations) {
		int i = 0;
		double x = 0;
		double y = 0;
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

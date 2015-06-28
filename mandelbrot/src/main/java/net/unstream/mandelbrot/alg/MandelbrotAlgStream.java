package net.unstream.mandelbrot.alg;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import net.unstream.mandelbrot.Fractal;

import org.apache.commons.math3.complex.Complex;

@Named("stream")
public class MandelbrotAlgStream implements MandelbrotAlg {
	
	/* (non-Javadoc)
	 * @see net.unstream.mandelbrot.MandelBrotAlg#compute(net.unstream.mandelbrot.Fractal, int)
	 */
	@Override
	public Map<Integer, double[]> compute(final Fractal fractal, final int width) {
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
				double nsmooth = MandelbrotIteration.iterate(c, fractal.getIterations());
				lines.get(y)[x] = nsmooth;
        	}        	
        });
		return lines;
	}



	
}

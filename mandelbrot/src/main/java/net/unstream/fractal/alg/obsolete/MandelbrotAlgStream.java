package net.unstream.fractal.alg.obsolete;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import net.unstream.fractal.alg.FractalAlg;
import net.unstream.fractal.api.domain.Fractal;

import org.apache.commons.math3.complex.Complex;

@Named("stream")
public class MandelbrotAlgStream implements FractalAlg {
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.MandelBrotAlg#compute(net.unstream.fractal.Fractal, int)
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
				int[] iterations = fractal.getColors().getIterations();
				double nsmooth = MandelbrotIteration.iterate(c, iterations[iterations.length - 1]);
				lines.get(y)[x] = nsmooth;
        	}        	
        });
		return lines;
	}



	
}

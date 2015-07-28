package net.unstream.mandelbrot.alg;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import javax.inject.Named;

import org.apache.commons.math3.complex.Complex;

import net.unstream.fractal.api.domain.Fractal;

@Named("forkjoin")
public class MandelbrotAlgForkJoin implements MandelbrotAlg {
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.MandelBrotAlg#compute(net.unstream.fractal.Fractal, int)
	 */
	@Override
	public Map<Integer, double[]> compute(final Fractal fractal, final int width) {
		Map<Integer, double[]> lines = new HashMap<Integer, double[]>();
		Complex c0 = new Complex(fractal.getC0(), fractal.getC0i());
		double step = (fractal.getC1() - fractal.getC0()) / width;
		int height = (int) Math.round((fractal.getC1i() - fractal.getC0i()) * width
				/ (fractal.getC1() - fractal.getC0()));
		for (int y = 0; y < height; y++) {
			lines.put(y, new double[width]);
		}
		int[] iterations = fractal.getColors().getIterations();
		MandelbrotTask fb = new MandelbrotTask(c0, step, 0, 0, width, height,
				iterations[iterations.length - 1], lines);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(fb);
		return fb.getLines();
	}
}

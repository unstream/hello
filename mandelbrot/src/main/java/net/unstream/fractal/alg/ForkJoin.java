package net.unstream.fractal.alg;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import javax.inject.Named;

import net.unstream.fractal.api.domain.Fractal;
import net.unstream.polynomial.PolynomialFunction;

import org.apache.commons.math3.complex.Complex;

@Named("forkjoin")
public class ForkJoin implements FractalAlg {
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.alg.FractalAlg#compute(net.unstream.fractal.Fractal, int)
	 */
	public Map<Integer, double[]> compute(final Fractal fractal, final int width) {
		Function<Complex, Double> iterateFunction = null;
		
		int[] iterations = fractal.getColors().getIterations();
		int maxIterations = iterations[iterations.length - 1];
		if (fractal.isRenderJulia()) {
			if (fractal.getPolynomial() == "") {
				iterateFunction = new JuliaFunction(maxIterations, new Complex(fractal.getcJulia(), fractal.getCiJulia()));
			} else {
				iterateFunction = new PolynomialFunction(maxIterations, fractal.getPolynomial());
			}
		} else {
			iterateFunction = new MandelbrotFunction(maxIterations);
		}
		
		Map<Integer, double[]> lines = new HashMap<Integer, double[]>();
		Complex z0 = new Complex(fractal.getC0(), fractal.getC0i());
		double step = (fractal.getC1() - fractal.getC0()) / width;
		int height = (int) Math.round((fractal.getC1i() - fractal.getC0i()) * width
				/ (fractal.getC1() - fractal.getC0()));
		for (int y = 0; y < height; y++) {
			lines.put(y, new double[width]);
		}
		
		FractalTask task = new FractalTask(iterateFunction, maxIterations, z0, step, 0, 0, width, height, lines);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(task);
		return task.getLines();
	}
}

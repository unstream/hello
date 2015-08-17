package net.unstream.fractal.alg;

import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;

@SuppressWarnings("serial")
public class FractalTask extends RecursiveAction {
	private static int sThreshold = 35;

	private Map<Integer, double[]> lines;

	final private Complex z0;
	final private int w;
	final private int h;

	final private double step;
	final private int x0;
	final private int y0;
	
	private boolean inside = true;

	private Function<Complex, Double> iterateFunction;

	final private int maxIterations;


	/**
	 * Create a new task for the given area defined by z0, the step and the width. 
	 * This area is either broken down further or computed directly using the given iterate-function
	 * for each point.
	 */
	public FractalTask(final Function<Complex, Double> myIterateFunction, int myMaxIterations, Complex myZ0,
			final double myStep, final int myX0, final int myY0, final int myW, final int myH, Map<Integer, double[]> myLines) {
		iterateFunction = myIterateFunction;
		this.maxIterations = myMaxIterations;
		z0 = myZ0;
		w = myW;
		h = myH;
		step = myStep;
		x0 = myX0;
		y0 = myY0;
		lines = myLines;
	}

	protected void compute() {

		if ((w <= sThreshold) || (h <= sThreshold)) {
			computeDirectly();
		} else {
			final int w0 = w / 2;
			final int w1 = w - w0;
			final int h0 = h / 2;
			final int h1 = h - h0;
			invokeAll(
				new FractalTask(iterateFunction, maxIterations, z0, step, x0, y0, w0, h0, lines), 
				new FractalTask(iterateFunction, maxIterations, z0, step, x0 + w0, y0, w1, h0, lines), 
				new FractalTask(iterateFunction, maxIterations, z0, step, x0, y0 + h0, w0, h1, lines), 
				new FractalTask(iterateFunction, maxIterations, z0, step, x0 + w0, y0 + h0, w1, h1, lines)
			);
		}
	}

	protected void computeDirectly() {
		computeDirectly(
				(Integer x, Integer y) 
				-> (x == x0) || (x == (x0 + w - 1)) || (y == y0) || (y == (y0 + h - 1))
			);
		if (inside) {
			fill();
		} else {
			computeDirectly((Integer x, Integer y) -> (x != x0)
					&& (x != (x0 + w - 1)) && (y != y0) && (y != (y0 + h - 1)));
		}
	}

	protected void computeDirectly(BiFunction<Integer, Integer, Boolean> f) {
		final double z0_r = z0.getReal() + (0.5d + x0) * step;
		final double z0_i = z0.getImaginary() + (0.5d + y0) * step;
		double z_r = z0_r;
		for (int x = x0; x < x0 + w; x++) {
			double z_i = z0_i;
			for (int y = y0; y < y0 + h; y++) {
				if (f.apply(x, y)) {
					double nsmooth = iterateFunction.apply(new Complex(z_r, z_i));
					if (!(nsmooth == ((double) maxIterations))) {
						inside = false;
					}
					lines.get(y)[x] = nsmooth;
				}
				z_i += step;
			}
			z_r += step;
		}
	}

	private void fill() {
		for (int x = x0 + 1; x < x0 + w - 1; x++) {
			for (int y = y0 + 1; y < y0 + h - 1; y++) {
				lines.get(y)[x] = maxIterations;
			}
		}
	}

	public Map<Integer, double[]> getLines() {
		return lines;
	}

	public static void setThreshold(int threshold) {
		sThreshold = threshold;
	}

}

package net.unstream.mandelbrot.alg;

import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.function.BiFunction;

import org.apache.commons.math3.complex.Complex;

@SuppressWarnings("serial")
public class MandelbrotTask extends RecursiveAction {
	private static int sThreshold = 35;

	private Map<Integer, double[]> lines;

	final private Complex c0;
	final private int w;
	final private int h;
	final private int iterations;

	final private double step;
	final private int x0;
	final private int y0;
	
	private boolean inside = true;

	public MandelbrotTask(final Complex myC0, final double myStep,
			final int myX0, final int myY0, final int myW, final int myH,
			final int myIterations, Map<Integer, double[]> myLines) {
		c0 = myC0;
		w = myW;
		h = myH;
		step = myStep;
		iterations = myIterations;
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
				new MandelbrotTask(c0, step, x0, y0, w0, h0, iterations, lines), 
				new MandelbrotTask(c0, step, x0 + w0, y0, w1, h0, iterations, lines), 
				new MandelbrotTask(c0, step, x0, y0 + h0, w0, h1, iterations, lines), 
				new MandelbrotTask(c0, step, x0 + w0, y0 + h0, w1, h1, iterations, lines)
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
		final double c_0 = c0.getReal() + (0.5d + x0) * step;
		final double ci_0 = c0.getImaginary() + (0.5d + y0) * step;
		final double dIterations = (double) iterations;
		double c1 = c_0;
		for (int x = x0; x < x0 + w; x++) {
			double ci = ci_0;
			for (int y = y0; y < y0 + h; y++) {
				if (f.apply(x, y)) {
					double nsmooth = MandelbrotIteration.iterate(new Complex(c1, ci), iterations);
					if (!(nsmooth == dIterations)) {
						inside = false;
					}
					lines.get(y)[x] = nsmooth;
				}
				ci += step;
			}
			c1 += step;
		}
	}

	private void fill() {
		for (int x = x0 + 1; x < x0 + w - 1; x++) {
			for (int y = y0 + 1; y < y0 + h - 1; y++) {
				lines.get(y)[x] = iterations;
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

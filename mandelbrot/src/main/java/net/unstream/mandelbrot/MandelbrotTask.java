package net.unstream.mandelbrot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.math3.complex.Complex;

@SuppressWarnings("serial")
public class MandelbrotTask extends RecursiveAction {
	protected static int sThreshold = 40;

	private Complex c0;
	final private int w;
	final private int h;
	final private int iterations;

	final private double step;
	private Map<Integer, double[]> lines;
	final private int y0;
	final private int x0;

	public MandelbrotTask(Fractal f, int width) {
		c0 = new Complex(f.getC0(), f.getC0i());
		w = width;
		iterations = f.getIterations();
		step = (f.getC1() - f.getC0()) / w;
		h = (int) Math.round((f.getC1i() - f.getC0i()) * w
				/ (f.getC1() - f.getC0()));
		x0 = 0;
		y0 = 0;
		lines = new HashMap<Integer, double[]>();
		for (int y = 0; y < h; y++) {
			lines.put(y, new double[w]);
		}
	}

	public MandelbrotTask(final Complex myC0, final double myStep,
			final int myX0, final int myY0, final int myW, final int myH,
			final int myIterations, Map<Integer, double[]> myDst) {
		c0 = myC0;
		w = myW;
		h = myH;
		step = myStep;
		iterations = myIterations;
		x0 = myX0;
		y0 = myY0;
		lines = myDst;
	}

	protected void compute() {
		if (w <= sThreshold) {
			computeDirectly();
			return;
		}

		final int w0 = w / 2;
		final int w1 = w - w0;
		final int h0 = h / 2;
		final int h1 = h - h0;

		invokeAll(
			new MandelbrotTask(c0, step, x0, y0, w1, h1, iterations, lines), 
			new MandelbrotTask(c0, step, x0 + w0, y0, w1, h1, iterations, lines), 
			new MandelbrotTask(c0, step, x0, y0 + w0, w1, h1, iterations, lines), 
			new MandelbrotTask(c0, step, x0	+ w0, y0 + w0, w1, h1, iterations, lines)
		);
	}

	protected void computeDirectly() {
		final double c_0 = c0.getReal() + (0.5d + x0) * step;
		final double ci_0 = c0.getImaginary() + (0.5d + y0) * step;
		double c = c_0;
		boolean open = false;
		// top
		c = c_0;
		for (int x = x0; x < x0 + w; x++) {
			double ci = ci_0;
			int y = y0;
			double nsmooth = iterate(new Complex(c, ci), iterations);
			lines.get(y)[x] = nsmooth;
			if (nsmooth != (double) iterations) {
				open = true;
			}
			c += step;
		}
		// bottom
		c = c_0;
		int y = y0 + h - 1;
		double ci = ci_0 + (h - 1) * step;
		for (int x = x0; x < x0 + w; x++) {
			double nsmooth = iterate(new Complex(c, ci), iterations);
			lines.get(y)[x] = nsmooth;
			if (nsmooth != (double) iterations) {
				open = true;
			}
			c += step;
		}

		// left
		c = c_0;
		int x = x0;
		ci = ci_0;
		for (y = y0; y < y0 + h; y++) {
			double nsmooth = iterate(new Complex(c, ci), iterations);
			lines.get(y)[x] = nsmooth;
			if (nsmooth != (double) iterations) {
				open = true;
			}
			ci += step;
		}

		// right
		c = c_0 + (w - 1) * step;
		x = x0 + w - 1;
		ci = ci_0;
		for (y = y0; y < y0 + h; y++) {
			double nsmooth = iterate(new Complex(c, ci), iterations);
			lines.get(y)[x] = nsmooth;
			if (nsmooth != (double) iterations) {
				open = true;
			}
			ci += step;
		}

		if (open) {
			c = c_0;
			for (x = x0 + 1; x < x0 + w - 1; x++) {
				ci = ci_0;
				for (y = y0 + 1; y < y0 + h - 1; y++) {
					double nsmooth = iterate(new Complex(c, ci), iterations);
					lines.get(y)[x] = nsmooth;
					ci += step;
				}
				c += step;
			}
		} else {
			c = c_0;
			for (x = x0 + 1; x < x0 + w - 1; x++) {
				ci = ci_0;
				for (y = y0 + 1; y < y0 + h - 1; y++) {
					lines.get(y)[x] = iterations;
				}
				c += step;
			}

		}
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
			nsmooth = 1d + i - Math.log(Math.log(Math.sqrt(x * x + y * y)))
					/ Math.log(2);
		}
		return nsmooth;
	}

	public Map<Integer, double[]> getLines() {
		return lines;
	}
}

package net.unstream.mandelbrot.alg;

import org.apache.commons.math3.complex.Complex;

public interface IterateFunctionInterface {
	public double iterate(int maxIterations, Complex ... parameters);
}

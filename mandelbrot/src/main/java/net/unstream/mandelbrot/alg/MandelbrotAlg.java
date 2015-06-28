package net.unstream.mandelbrot.alg;

import java.util.Map;

import net.unstream.mandelbrot.Fractal;

public interface MandelbrotAlg {

	public abstract Map<Integer, double[]> compute(Fractal fractal, int width);

}
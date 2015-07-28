package net.unstream.mandelbrot.alg;

import java.util.Map;

import net.unstream.fractal.api.domain.Fractal;

public interface MandelbrotAlg {

	public abstract Map<Integer, double[]> compute(Fractal fractal, int width);

}
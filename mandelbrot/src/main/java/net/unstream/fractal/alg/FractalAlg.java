package net.unstream.fractal.alg;

import java.util.Map;

import net.unstream.fractal.api.domain.Fractal;

public interface FractalAlg {

	public abstract Map<Integer, double[]> compute(Fractal fractal, int width);

}
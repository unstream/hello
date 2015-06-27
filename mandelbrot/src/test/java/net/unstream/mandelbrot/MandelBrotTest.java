package net.unstream.mandelbrot;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import org.junit.Test;

public class MandelBrotTest {

	@Test
	public void testZeroZero() {
		Fractal f = new Fractal();
		f.setIterations(100);
		f.setC0(0d);
		f.setC0i(0d);
		f.setC1(0.001d);
		f.setC1i(0.001d);
		Map<Integer, double[]> lines = MandelBrot.compute(f, 1);
		assertTrue(lines.get(0)[0] == (double) f.getIterations());
	}

	@Test
	public void testTwoZero() {
		Fractal f = new Fractal();

		f.setIterations(100);
		f.setC0(2d);
		f.setC0i(0d);
		f.setC1(2.001d);
		f.setC1i(0.001d);
		Map<Integer, double[]> lines = MandelBrot.compute(f, 1);
		assertTrue(lines.get(0)[0] < 50);
	}

	@Test
	public void testSpeed1() {
		Fractal f = new Fractal();

		f.setIterations(1000);
		long now = System.currentTimeMillis();
		Map<Integer, double[]> lines = MandelBrot.compute(f, 1000);
		System.out.println("TimeStream: " + (System.currentTimeMillis() - now));
	}
	
	@Test
	public void testSpeed2() {
		Fractal f = new Fractal();

		f.setIterations(1000);
		
		long now = System.currentTimeMillis();		
		MandelbrotTask fb = new MandelbrotTask(f, 1000);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(fb);


		Map<Integer, double[]> lines = fb.getLines();
		System.out.println("Time ForkJoin: " + (System.currentTimeMillis() - now));
	}


	
}

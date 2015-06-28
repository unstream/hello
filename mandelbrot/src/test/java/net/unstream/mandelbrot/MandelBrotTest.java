package net.unstream.mandelbrot;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import javax.inject.Inject;
import javax.inject.Named;

import net.unstream.mandelbrot.alg.MandelbrotAlg;
import net.unstream.mandelbrot.alg.MandelbrotAlgForkJoin;
import net.unstream.mandelbrot.alg.MandelbrotAlgStream;
import net.unstream.mandelbrot.alg.MandelbrotTask;

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
		Map<Integer, double[]> lines = new MandelbrotAlgForkJoin().compute(f, 1);
		assertTrue(lines.get(0)[0] == (double) f.getIterations());
	}

	@Test
	public void testTwoZero() {
		MandelbrotAlg alg = null;
		Fractal f = new Fractal();

		f.setIterations(100);
		f.setC0(2d);
		f.setC0i(0d);
		f.setC1(2.001d);
		f.setC1i(0.001d);
		Map<Integer, double[]> lines = new MandelbrotAlgForkJoin().compute(f, 1);
		assertTrue(lines.get(0)[0] < 50);
	}

	/**
	 * 
Time[MandelBrotAlgStream]: 2302
Time[MandelBrotAlgForkJoin]: 824
Time[MandelBrotAlgStream]: 2120
Time[MandelBrotAlgForkJoin]: 663
Time[MandelBrotAlgStream]: 2022
Time[MandelBrotAlgForkJoin]: 651
Time[MandelBrotAlgStream]: 2179
Time[MandelBrotAlgForkJoin]: 671
Time[MandelBrotAlgStream]: 2111
Time[MandelBrotAlgForkJoin]: 683
	 */
	@Test
	public void testBenchmark() {
		int[] iterations = {10, 100, 200, 500};
		int[] sizes = {100, 200, 400, 800, 1600};
		MandelbrotAlg[] algorithms = {new MandelbrotAlgStream(), new MandelbrotAlgForkJoin()};
		for (int threshold = 35; threshold < 38; threshold += 1) {
//			MandelbrotTask.setThreshold(threshold);
			for (MandelbrotAlg alg: algorithms) {
				long start = System.currentTimeMillis();
				for (int i : iterations) {
					for(int w: sizes) {
						Fractal f = new Fractal();
						f.setIterations(i);
						Map<Integer, double[]> lines = alg.compute(f, w);
					}
				}
				System.out.println("Time[" + alg.getClass().getSimpleName() + ", " + threshold + "]: "
						+ (System.currentTimeMillis() - start));
			}
		}
	}
	@Test
	public void testSmall() {
						Fractal f = new Fractal();
						f.setC0(0d);
						f.setC0i(0d);
						f.setC1(0.001d);
						f.setC1i(0.001d);
						f.setIterations(100);
						Map<Integer, double[]> lines = new MandelbrotAlgForkJoin().compute(f, 5);
	}
	
}

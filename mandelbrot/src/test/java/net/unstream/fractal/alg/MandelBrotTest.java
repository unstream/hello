package net.unstream.fractal.alg;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import net.unstream.fractal.alg.ForkJoin;
import net.unstream.fractal.alg.obsolete.MandelbrotAlgForkJoin;
import net.unstream.fractal.alg.obsolete.MandelbrotAlgStream;
import net.unstream.fractal.alg.obsolete.MandelbrotTask;
import net.unstream.fractal.api.domain.Colors;
import net.unstream.fractal.api.domain.Fractal;

import org.junit.Test;

public class MandelBrotTest {

	@Test
	public void testZeroZero() {
		Fractal f = new Fractal();
		f.setColors(twoColors(100));
		f.setC0(0d);
		f.setC0i(0d);
		f.setC1(0.001d);
		f.setC1i(0.001d);
		Map<Integer, double[]> lines = new MandelbrotAlgForkJoin().compute(f, 1);
		assertTrue(lines.get(0)[0] == 100d);
	}

	@Test
	public void testTwoZero() {
		Fractal f = new Fractal();
		f.setColors(twoColors(100));
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
		int[] iterations = {10, 100};
		int[] sizes = {100, 200, 400};
		FractalAlg[] algorithms = {new MandelbrotAlgStream(), new MandelbrotAlgForkJoin(), new ForkJoin()};
		for (int threshold = 20; threshold <= 80; threshold += 20) {
			MandelbrotTask.setThreshold(threshold);
			for (FractalAlg alg: algorithms) {
				long start = System.currentTimeMillis();
				for (int i : iterations) {
					for(int w: sizes) {
						Fractal f = new Fractal();
						f.setColors(twoColors(i));
						alg.compute(f, w);
					}
				}
				System.out.println("Time[" + alg.getClass().getSimpleName() + ", " + threshold + "]: "
						+ (System.currentTimeMillis() - start));
			}
		}
	}
	
	private Colors twoColors(final int i) {
		Colors c = new Colors(); 
		c.setIterations(new int []{0, i});
		c.setColors(new String []{"#000000", "#ffffff"});
		return c;
	}

	@Test
	public void testSmall() {
						Fractal f = new Fractal();
						f.setC0(0d);
						f.setC0i(0d);
						f.setC1(0.001d);
						f.setC1i(0.001d);
						f.setColors(twoColors(100));
						new MandelbrotAlgForkJoin().compute(f, 5);
	}
	
	@Test
	public void testJulia() {
						Fractal f = new Fractal();
						f.setC0(-2d);
						f.setC0i(-2d);
						f.setC1(2d);
						f.setC1i(2d);
						f.setColors(twoColors(100));
						f.setRenderJulia(true);
						f.setcJulia(0d);
						f.setCiJulia(0d);
						new ForkJoin().compute(f, 500);
	}

}

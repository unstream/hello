package net.unstream.fractal.alg;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import net.unstream.fractal.alg.obsolete.MandelbrotAlgStream;
import net.unstream.fractal.api.domain.Colors;
import net.unstream.fractal.api.domain.Fractal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AlgorithmsTestContext.class})
public class MandelBrotTest {
	
	@Inject @Named(value="forkjoin")
	FractalAlg alg;

	@Test
	public void testMandelbrotZeroZero() {
		Fractal f = new Fractal();
		f.setColors(twoColors(100));
		f.setC0(0d);
		f.setC0i(0d);
		f.setC1(0.001d);
		f.setC1i(0.001d);
		Map<Integer, double[]> lines = alg.compute(f, 1);
		assertTrue(lines.get(0)[0] == 100d);
	}

	@Test
	public void testMandelbrotTwoZero() {
		Fractal f = new Fractal();
		f.setColors(twoColors(100));
		f.setC0(2d);
		f.setC0i(0d);
		f.setC1(2.001d);
		f.setC1i(0.001d);
		Map<Integer, double[]> lines = alg.compute(f, 1);
		assertTrue(lines.get(0)[0] < 50);
	}

	@Test
	public void testBenchmark() {
		int[] iterations = {10, 100};
		int[] sizes = {100, 200, 400};
		FractalAlg[] algorithms = {new MandelbrotAlgStream(), alg};
		for (int threshold = 20; threshold <= 80; threshold += 20) {
			FractalTask.setThreshold(threshold);
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
						alg.compute(f, 5);
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

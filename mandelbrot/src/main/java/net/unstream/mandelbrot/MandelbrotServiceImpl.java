package net.unstream.mandelbrot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.inject.Named;

import org.apache.commons.math3.complex.Complex;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

@Named
public class MandelbrotServiceImpl implements MandelbrotService {
	
	private static final int COLOR_MAP_SIZE = 2000;
	private final static Logger LOG = LoggerFactory.getLogger(MandelbrotServiceImpl.class);

	public MandelbrotServiceImpl() {
	}
	
	/* (non-Javadoc)
	 * @see net.unstream.mandelbrot.MandelbrotService#computeMandelBrotPng(net.unstream.mandelbrot.Fractal)
	 */
	@Async
	public Future<byte[]> computeMandelBrotPng(final Fractal fractal) throws MandelbrotServiceException {
		try {
			long now = System.currentTimeMillis();
			LOG.info("Starting to compute image ... ");
			BufferedImage image = createMandelBrotImage(fractal);
			LOG.info("Completed in " + (System.currentTimeMillis() - now) + " ms.");

	        final ImageFormat format = ImageFormat.IMAGE_FORMAT_PNG;
	        final Map<String, Object> optionalParams = new HashMap<String, Object>();
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
			Sanselan.writeImage(image, os, format, optionalParams);
			return new AsyncResult<byte[]>(os.toByteArray());
		} catch (ImageWriteException | IOException e) {
			LOG.error(e.getMessage(), e);
			throw new MandelbrotServiceException(e);
		}
		
	}
	/* (non-Javadoc)
	 * @see net.unstream.mandelbrot.MandelbrotService#computeColorGradientPng(net.unstream.mandelbrot.Fractal)
	 */
	public byte[] computeColorGradientPng(final Color... colors) throws MandelbrotServiceException {
		try {
			Color[] map = generateColorMap(colors);
			
			final ImageFormat format = ImageFormat.IMAGE_FORMAT_PNG;
	        final Map<String, Object> optionalParams = new HashMap<String, Object>();
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        BufferedImage image = new BufferedImage(map.length, 1, BufferedImage.TYPE_3BYTE_BGR);
	        for (int i = 0; i < map.length; i++) {
	        	image.setRGB(i, 0, map[i].getRGB());
	        }
			Sanselan.writeImage(image, os, format, optionalParams);
			return os.toByteArray();
		} catch (ImageWriteException | IOException e) {
			LOG.error(e.getMessage(), e);
			throw new MandelbrotServiceException(e);
		}
		
	}

	
	private BufferedImage createMandelBrotImage(Fractal fractal) {
		final Complex c0 = new Complex(fractal.getC0(), fractal.getC0i());
		final Complex c1 = new Complex(fractal.getC1(), fractal.getC1i());
		final int width = fractal.getWidth();

		final BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_3BYTE_BGR);
        final double xStep = c1.subtract(c0).getReal() / width;
        final double yStep = c1.subtract(c0).getImaginary() / width;
                 
        class Line {
        	public Line(int myY) {
				this.y = myY;
			}
			int y;
        	int [] line;
        }
        Color[] colorMap = generateColorMap(
        		Color.decode(fractal.getColor1()),
        		Color.decode(fractal.getColor2()),
        		Color.decode(fractal.getColor3())
        );
        
        Set<Line> lines = new HashSet<Line>();
        for (int y = 0; y < width; y++) {
        	lines.add(new Line(y));
        }
        lines.parallelStream().forEach((line) -> {
        	double ic = yStep * line.y;
        	line.line = new int[width];
        	for (int x = 0; x < width; x++) {             	
				Complex c = c0.add(new Complex(xStep * x, ic));
				double nsmooth = iterate(c, fractal.getIterations());

				Color color = mapColor(nsmooth, colorMap, fractal);
				line.line[x] = color.getRGB();
        	}        	
        });
        for (Line line: lines) {
        	image.setRGB(0, line.y, width, 1, line.line, 0, width);
        }
		return image;
	}

	private Color mapColor(final double nsmooth, final Color[] colorMap, final Fractal fractal) {
		int i = (int) Math.round(1d * nsmooth * colorMap.length / fractal.getIterations());
		if (i < 0) {
			i = 0;
		} else if (i >= colorMap.length) {
			i = colorMap.length - 1;
		}
		return colorMap[i];
	}

	private Color[] generateColorMap(Color... colors) {
		Color[] colorMap = new Color[COLOR_MAP_SIZE];
        for (int i = 0; i < COLOR_MAP_SIZE; i++) {
        	float r = 1f * (colors.length - 1) * i / COLOR_MAP_SIZE;
        	int i0 = (int) Math.floor(r);
        	int i1 = i0 + 1;
        	r = r - i0;
        	
        	int red = (int) ((1f - r) * colors[i0].getRed()   + r * colors[i1].getRed());
			int green = (int) ((1f - r) * colors[i0].getGreen() + r * colors[i1].getGreen());
			int blue = (int) ((1f - r) * colors[i0].getBlue()  + r * colors[i1].getBlue());
			Color color = new Color(red, green, blue);
			colorMap[i] = color;
        }
		return colorMap;
	}
			
	private double iterate(final Complex c, final int maxIterations) {
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
			nsmooth = 1d + i - Math.log(Math.log(Math.sqrt(x*x + y*y)))/Math.log(2);
		}
		return nsmooth;
	}
}

package net.unstream.mandelbrot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import javax.inject.Named;

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

	public Future<byte[]> computeMandelBrotPng(final Fractal fractal,
			final int width, final int height)
			throws MandelbrotServiceException {

		try {
			long now = System.currentTimeMillis();
			LOG.info("Starting to compute image ... ");
			BufferedImage image = createMandelBrotImage(fractal, width, height);
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
	
	private BufferedImage createMandelBrotImage(final Fractal fractal, final int width, final int height) {
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Color[] colorMap = generateColorMap(
        		Color.decode(fractal.getColor1()),
        		Color.decode(fractal.getColor2()),
        		Color.decode(fractal.getColor3())
        );
        Map<Integer, double[]> lines;
//        lines = MandelBrot.compute(fractal, width);
		MandelbrotTask fb = new MandelbrotTask(fractal, width);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(fb);
		lines = fb.getLines();
		
        for (int y: lines.keySet()) {
        	for (int x = 0; x < width; x++) {
        		double nsmooth = lines.get(y)[x];
        		Color color = mapColor(nsmooth, colorMap, fractal);
        		image.setRGB(x, y, color.getRGB());
        	}
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
	
}

package net.unstream.fractal;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;

import net.unstream.fractal.api.MandelbrotService;
import net.unstream.fractal.api.MandelbrotServiceException;
import net.unstream.fractal.api.domain.Colors;
import net.unstream.fractal.api.domain.Fractal;
import net.unstream.mandelbrot.alg.JuliaAlg;
import net.unstream.mandelbrot.alg.MandelbrotAlg;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

@Named
public class MandelbrotServiceImpl implements MandelbrotService {
	
	private static final int COLOR_MAP_SIZE = 10000;
	private final static Logger LOG = LoggerFactory.getLogger(MandelbrotServiceImpl.class);
	
	@Inject @Named("forkjoin")
	private final MandelbrotAlg alg = null;
	@Inject	@Named("forkjoinJulia")
	private final MandelbrotAlg algJulia = null;
	
	public MandelbrotServiceImpl() {
	}
	
	/* (non-Javadoc)
	 * @see net.unstream.fractal.MandelbrotService#computeMandelBrotPng(net.unstream.fractal.Fractal)
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
	 * @see net.unstream.fractal.MandelbrotService#computeColorGradientPng(net.unstream.fractal.Fractal)
	 */
	public byte[] computeColorGradientPng(final Colors colors) throws MandelbrotServiceException {
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
		
		Color[] colorMap = generateColorMap(fractal.getColors()); 
				
        Map<Integer, double[]> lines;
        if (!fractal.isRenderJulia()) {
            lines = alg.compute(fractal, width);
        } else {
            lines = algJulia.compute(fractal, width);
        }
		for (int y = 0; y < Math.min(height, lines.size()); y++) {
        	for (int x = 0; x < width; x++) {
        		double nsmooth = lines.get(y)[x];
        		Color color = mapColor(nsmooth, colorMap, fractal.getColors());
        		image.setRGB(x, y, color.getRGB());
        	}
        }
		return image;
	}
	
	private Color mapColor(final double nsmooth, final Color[] colorMap, final Colors colors) {
		int[] cis = colors.getIterations();
		int i = (int) Math.round( (nsmooth - cis[0]) * colorMap.length / (cis[cis.length - 1] - cis[0]));
		
		if (i < 0) {
			i = 0;
		} else if (i >= colorMap.length) {
			i = colorMap.length - 1;
		}
		return colorMap[i];
	}

	/**
	 * Generate a color gradient. It starts with the first color and ends with the last color.
	 * @param colors Defines the colors for the iterations.
	 * @return Color map
	 */
	private Color[] generateColorMap(Colors colors) {
		final Color[] colorMap = new Color[COLOR_MAP_SIZE];
		final int iMax = colors.getIterations()[colors.getIterations().length - 1];
		int idx = 0;
		int it0 = colors.getIterations()[0] - 1;
		int it1 = colors.getIterations()[0];
		Color c0 = Color.WHITE;
		Color c1 = Color.BLACK;
        for (int i = 0; i < COLOR_MAP_SIZE; i++) {
        	//compute current iteration
        	float currentIteration = 0.0f + colors.getIterations()[0] + (iMax - colors.getIterations()[0]) * i / (float) COLOR_MAP_SIZE;   
        	if (currentIteration >= it1) {
        		idx += 1;
        		it0 = colors.getIterations()[idx - 1];
        		it1 = colors.getIterations()[idx];
        		c0 = Color.decode(colors.getColors()[idx - 1]);
        		c1 = Color.decode(colors.getColors()[idx]);
        	}
        	/* 
        	 * Interpolate between the colors c0 and c1 
        	 */
        	float r = 1.0f * (currentIteration - it0) / (it1 - it0);

        	int red = (int) ((1f - r) * c0.getRed()   + r * c1.getRed());
			int green = (int) ((1f - r) * c0.getGreen() + r * c1.getGreen());
			int blue = (int) ((1f - r) * c0.getBlue()  + r * c1.getBlue());
			Color color = new Color(red, green, blue);
			colorMap[i] = color;
        }
		return colorMap;
	}
	
}

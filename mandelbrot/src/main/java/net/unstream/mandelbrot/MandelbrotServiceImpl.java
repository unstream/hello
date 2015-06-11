package net.unstream.mandelbrot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.apache.commons.math3.complex.Complex;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class MandelbrotServiceImpl implements MandelbrotService {
	
	private final static Logger LOG = LoggerFactory.getLogger(MandelbrotServiceImpl.class);

	public MandelbrotServiceImpl() {
	}
	
	public byte[] computeMandelBrotPng(final double c0, final double c0i,
			final double c1, final double c1i, final int iterations,
			final int width) throws MandelbrotServiceException {
		try {
			long now = System.currentTimeMillis();
			LOG.info("Starting to compute image ... ");
			BufferedImage image = createMandelBrotImage(new Complex(c0, c0i), new Complex(c1, c1i), iterations, width);
			LOG.info("Completed in " + (System.currentTimeMillis() - now) + " ms.");

	        final ImageFormat format = ImageFormat.IMAGE_FORMAT_PNG;
	        final Map<String, Object> optionalParams = new HashMap<String, Object>();
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
			Sanselan.writeImage(image, os, format, optionalParams);
			return os.toByteArray();
		} catch (ImageWriteException | IOException e) {
			LOG.error(e.getMessage(), e);
			throw new MandelbrotServiceException(e);
		}
		
	}
	
	private BufferedImage createMandelBrotImage(final Complex c0, final Complex c1, final int maxIterations, final int width) {
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
        
        Set<Line> lines = new HashSet<Line>();
        for (int y = 0; y < width; y++) {
        	lines.add(new Line(y));
        }
        lines.parallelStream().forEach((line) -> {
        	double ic = yStep * line.y;
        	line.line = new int[width];
        	for (int x = 0; x < width; x++) {             	
				Complex c = c0.add(new Complex(xStep * x, ic));
				int iterations = iterate(c, maxIterations);
         		if (maxIterations == iterations) {
         			line.line[x] = Color.black.getRGB();
         		} else {
         			int r = 255 - (255 * iterations / maxIterations);
         			line.line[x] = new Color(r, r, r).getRGB();
         		}  
            }        	
        });
        for (Line line: lines) {
        	image.setRGB(0, line.y, width, 1, line.line, 0, width);
        }
		return image;
	}
			
	private int iterate(final Complex c, final int maxIterations) {
		int i = 0;
		double x = 0;
		double y = 0;
		double x2, y2;
		do {
			x2 = x * x;
			y2 = y * y;
			if ((x2 + y2) > 4) {
				break;
			}
			y = 2 * x * y + c.getImaginary();
			x = x2 - y2 + c.getReal();
			i++;
		} while (i < maxIterations);
		return i;
	}
}

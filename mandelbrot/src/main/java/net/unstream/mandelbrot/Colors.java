package net.unstream.mandelbrot;

import java.util.Arrays;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Colors {
	@GraphId Long id;

	private String [] colors = {"#000000", "#0080ff", "#30b0ff", "#ffffff"} ;

	private int [] iterations = {0, 50, 100, 200};

	private boolean useCyclicColors;

	public void addColor(final String color, final int iteration) {
		final int length = colors.length;
		colors = Arrays.copyOf(colors, length + 1);
		iterations = Arrays.copyOf(iterations, length + 1);
		colors[length] = color;
		iterations[length] = iteration;
	}
	
	public String[] getColors() {
		return colors;
	}

	public void setColors(String[] colors) {
		this.colors = colors;
	}

	public int[] getIterations() {
		return iterations;
	}

	public void setIterations(int[] iterations) {
		this.iterations = iterations;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isUseCyclicColors() {
		return useCyclicColors;
	}

	public void setUseCyclicColors(boolean useCyclicColors) {
		this.useCyclicColors = useCyclicColors;
	}
	
}

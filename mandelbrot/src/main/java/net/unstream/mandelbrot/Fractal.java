package net.unstream.mandelbrot;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Fractal {
    @GraphId Long id;

	private String name = "";
	private String description = "";
    
	private double c0 = -1.5d;
	private double c0i = -1d;
	private double c1 = 0.5d;
	private double c1i = 1d;
	private int width = 500;
	private int height = 500;
	private int iterations = 100;
	private String color1 = "#000000";
	private String color2 = "#00aabb";
	private String color3 = "#ffffff";
	private String imageUUID = null;
	private byte [] image = null;

	@RelatedTo(type="Parent", direction=Direction.OUTGOING)
    public @Fetch Fractal parent;
	
	public String getColor1() {
		return color1;
	}
	public void setColor1(String color1) {
		this.color1 = color1;
	}
	public String getColor2() {
		return color2;
	}
	public void setColor2(String color2) {
		this.color2 = color2;
	}
	public String getColor3() {
		return color3;
	}
	public void setColor3(String color3) {
		this.color3 = color3;
	}
	public double getC0() {
		return c0;
	}
	public void setC0(double c0) {
		this.c0 = c0;
	}
	public double getC0i() {
		return c0i;
	}
	public void setC0i(double c0i) {
		this.c0i = c0i;
	}
	public double getC1() {
		return c1;
	}
	public void setC1(double c1) {
		this.c1 = c1;
	}
	public double getC1i() {
		return c1i;
	}
	public void setC1i(double c1i) {
		this.c1i = c1i;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getIterations() {
		return iterations;
	}
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Fractal getParent() {
		return parent;
	}
	public void setParent(Fractal parent) {
		this.parent = parent;
	}

    public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public String getImageUUID() {
		return imageUUID;
	}
	public void setImageUUID(String imageUUID) {
		this.imageUUID = imageUUID;
	}
	
}

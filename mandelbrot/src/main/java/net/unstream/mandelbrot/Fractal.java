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

	@RelatedTo(type="Parent", direction=Direction.OUTGOING)
    private @Fetch Fractal parent;

    @RelatedTo(type="THUMB", direction=Direction.OUTGOING)
    private @Fetch Image thumbnail;

    @RelatedTo(type="IMAGE", direction=Direction.OUTGOING)
    private Image image;

    @RelatedTo(type="COLORMAPPING", direction=Direction.OUTGOING)
    private @Fetch Colors colors;

	public Fractal() {
    	colors = new Colors();
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
    public Image getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public Colors getColors() {
		return colors;
	}
	public void setColors(Colors colors) {
		this.colors = colors;
	}

}

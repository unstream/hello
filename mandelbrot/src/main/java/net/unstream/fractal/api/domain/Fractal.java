package net.unstream.fractal.api.domain;

import org.springframework.data.annotation.Id;

public class Fractal {
	@Id
	String id;
	
	private String name = "";
	private String description = "";
    
	private double c0 = -1.5d;
	private double c0i = -1d;
	private double c1 = 0.5d;
	private double c1i = 1d;

	private boolean renderJulia = false;
	private double cJulia = 0d;
	private double ciJulia = 0d;
	
	private String polynomial ="";

    private Fractal parent;
    private Image thumbnail;

    private Image image;

    private User creator;

    private Colors colors;

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

	public boolean isRenderJulia() {
		return renderJulia;
	}
	public void setRenderJulia(boolean renderJulia) {
		this.renderJulia = renderJulia;
	}
	public double getcJulia() {
		if (!isRenderJulia()) {
			setcJulia(getC0() + 0.5d * (getC1() - getC0()));
		}
		return cJulia;
	}
	public void setcJulia(double cJulia) {
		this.cJulia = cJulia;
	}
	public double getCiJulia() {
		if (!isRenderJulia()) {
			setCiJulia(getC0i() + 0.5d * (getC1i() - getC0i()));
		}
		return ciJulia;
	}
	public void setCiJulia(double ciJulia) {
		this.ciJulia = ciJulia;
	}

	public String getPolynomial() {
		return polynomial;
	}
	public void setPolynomial(String polynomial) {
		this.polynomial = polynomial;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
    public User getCreator() {
    	User user = creator;
    	if (user == null) {
    		user = new User();
    		user.setUsername("anonymous");
    	}
		return user;
	}
	public void setCreator(User creator) {
		this.creator = creator;
	}
	public Colors getColors() {
		return colors;
	}
	public void setColors(Colors colors) {
		this.colors = colors;
	}

}

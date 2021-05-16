package net.unstream.fractal.api.domain;

import org.springframework.data.annotation.Id;

public class Image {
	public enum 		ImageType {
		PNG
	}
	@Id
    private String id;
	
	private int width = 500;
	private int height = 500;
	private ImageType type = ImageType.PNG;
	private byte [] image = null;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public ImageType getType() {
		return type;
	}
	public void setType(ImageType type) {
		this.type = type;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
}

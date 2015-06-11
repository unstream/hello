package com.test.hello.domain;

public class Fractal {
	double c0 = -1.5d;
	double c0i = -1d;
	double c1 = 0.5d;
	double c1i = 1d;
	int width = 500;
	int height = 500;
	int iterations = 100;

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
}

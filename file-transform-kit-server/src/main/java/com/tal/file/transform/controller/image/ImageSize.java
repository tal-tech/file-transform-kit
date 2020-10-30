package com.tal.file.transform.controller.image;

public class ImageSize {

	private int width = 0;
	private int height = 0;

	public ImageSize(){

	}

	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
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

}

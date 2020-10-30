package com.tal.file.transform.image;

import com.tal.file.transform.controller.image.ImageSize;

public class ImageTask {

	private String operate = "";
	private ImageSize limitSize = null;
	private ImageSize size = null;
	private ImageSize minSize = null;
	private ImageSize maxSize = null;

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public ImageSize getLimitSize() {
		return limitSize;
	}

	public void setLimitSize(ImageSize limitSize) {
		this.limitSize = limitSize;
	}

	public ImageSize getSize() {
		return size;
	}

	public void setSize(ImageSize size) {
		this.size = size;
	}

	public void setSize(int width, int height) {
		this.size = new ImageSize(width, height);
	}

	public ImageSize getMinSize() {
		return minSize;
	}

	public void setMinSize(ImageSize minSize) {
		this.minSize = minSize;
	}

	public void setMinSize(int width, int height) {
		this.minSize = new ImageSize(width, height);
	}

	public ImageSize getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(ImageSize maxSize) {
		this.maxSize = maxSize;
	}

	public void setMaxSize(int width, int height) {
		this.maxSize = new ImageSize(width, height);
	}

}

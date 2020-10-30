package com.tal.file.transform.controller.image;

import com.tal.file.transform.common.mime.Mime;

import java.awt.image.BufferedImage;

public class ImageJob {

	private BufferedImage img = null;
	private String storPath = "";
	private String bizName = "";
	private Mime mime = null;

	public ImageJob(BufferedImage img, String storPath, String bizName, Mime mime) {
		this.img = img;
		this.storPath = storPath;
		this.bizName = bizName;
		this.mime = mime;
	}

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public String getStorPath() {
		return storPath;
	}

	public void setStorPath(String storPath) {
		this.storPath = storPath;
	}

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}

	public Mime getMime() {
		return mime;
	}

	public void setMime(Mime mime) {
		this.mime = mime;
	}

}

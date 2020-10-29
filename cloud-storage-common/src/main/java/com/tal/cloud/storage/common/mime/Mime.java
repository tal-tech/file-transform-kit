package com.tal.cloud.storage.common.mime;

/**
 * MIME 实体类
 * 
 * @author lazycathome
 *
 */
public class Mime {

	private String mimeType = "application/octet-stream";
	private String fileExt = ".unk";

	public Mime() {
		
	}

	public Mime(String fileExt) {
		this.fileExt = fileExt;
	}

	public Mime(String fileExt, String mimeType) {
		this.fileExt = fileExt;
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getFileExt() {
		return fileExt;
	}

}

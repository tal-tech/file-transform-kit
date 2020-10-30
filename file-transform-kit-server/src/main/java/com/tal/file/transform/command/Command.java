package com.tal.file.transform.command;

import com.tal.file.transform.common.storage.StorageFile;

import java.io.File;

public class Command {

	public static final String TYPE_UNKNOWN = "unknown";
	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_AUDIO = "audio";
	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_MISC = "misc";

	private String type = TYPE_UNKNOWN;
	private String filename = "";
	private File local = null;
	private StorageFile target = null;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public File getLocal() {
		return local;
	}

	public void setLocal(File local) {
		this.local = local;
	}

	public StorageFile getTarget() {
		return target;
	}

	public void setTarget(StorageFile target) {
		this.target = target;
	}

}

package com.tal.cloud.storage.node.controller.result;

import java.util.ArrayList;
import java.util.List;

import com.tal.cloud.storage.common.rest.ApiResult;

public class UploadResult<T> extends ApiResult {

	private List<T> files = new ArrayList<T>();

	public List<T> getFiles() {
		return files;
	}

	public void setFiles(List<T> files) {
		this.files = files;
	}

	public void addFile(T file) {
		this.files.add(file);
	}

}

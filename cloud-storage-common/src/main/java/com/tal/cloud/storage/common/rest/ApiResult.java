package com.tal.cloud.storage.common.rest;

/**
 * RESTful API 的标准返回
 * 
 * @author lazycathome
 *
 */
public class ApiResult {

	private int code = 0;
	private String message = "ok";

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

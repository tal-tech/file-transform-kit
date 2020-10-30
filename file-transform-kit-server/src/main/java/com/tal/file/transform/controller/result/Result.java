package com.tal.file.transform.controller.result;

public interface Result {

	int ERR_OK = 0;
	int ERR_NOT_SUPPORTED_TYPE = 90001;
	int ERR_IS_NOT_MULTIPART = 90002;
	int ERR_UPLOAD_FAILED = 90003;
	int ERR_NOT_ENOUGH_DATA = 90004;
	int ERR_AUTHENTICATION_FAILED = 90005;

	String MSG_OK = "ok";
	String MSG_NOT_SUPPORTED_TYPE = "Not supported type.";
	String MSG_IS_NOT_MULTIPART = "Enctype of the FORM is not Multi/Part.";
	String MSG_UPLOAD_FAILED = "Upload failed.";
	String MSG_NOT_ENOUGH_DATA = "Not enough data.";
	String MSG_AUTHENTICATION_FAILED = "Authentication failed.";

	void render(Object result);
	void render(String result);

}

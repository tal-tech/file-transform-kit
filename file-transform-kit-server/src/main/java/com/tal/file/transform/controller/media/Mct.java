package com.tal.file.transform.controller.media;


/**
 * 
 * @author liujt
 *
 */
public interface Mct {

	String KEY_SELF = "_self";
	String KEY_TRANSFER = "_t";
	String KEY_THUMBNAIL = "_thumb";
	String KEY_META = "_meta";

	int OPERATE_TRANSFER_AUDIO = 1;
	int OPERATE_TRANSFER_VIDEO = 2;
	int OPERATE_THUMBNAIL = 3;

	long getDuration(String storPath);
	String transfer(String storPath, String pipeline, String preset, String mime);
	String thumbnail(String storPath, String mime);

}

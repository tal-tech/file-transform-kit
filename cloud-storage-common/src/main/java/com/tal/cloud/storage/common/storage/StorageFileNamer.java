package com.tal.cloud.storage.common.storage;

import java.util.Date;

import com.tal.cloud.storage.common.EncryptUtils;
import com.tal.cloud.storage.common.FilenameUtils;

/**
 * Storage 文件名生成工厂
 * 
 * @author lazycathome
 *
 */
public class StorageFileNamer {

	private String filePath = "";

	/**
	 * 指定前缀和文件名，生成新的文件对象名称
	 * @param prefix
	 * @param origFilename
	 * @return
	 */
	public String bename(String prefix, String origFilename) {
		Date now = new Date();
		String fn = EncryptUtils.Md5(origFilename);
		String ext = FilenameUtils.getExt(origFilename);

		return String.format("/%s/%s/%tY%tm%td/%tH/%tM%tS.%s%s", prefix, ext.replace(".", ""), now, now, now, now, now, now, fn, ext).toLowerCase();
	}

	/**
	 * 指定文件名，生成新的文件对象名称
	 * @param origFilename
	 * @return
	 */
	public String bename(String origFilename) {
		return bename("", origFilename);
	}

	/**
	 * Sets filePath.
	 *
	 * @param filePath the filePath
	 */
	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Gets filePath.
	 *
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
}

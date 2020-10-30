package com.tal.file.transform.common.storage;

import java.io.InputStream;

import com.tal.file.transform.common.mime.Mime;

/**
 * Storage 文件操作接口
 * 
 * @author lazycathome
 *
 */
public interface StorageFile {

	/**
	 * 获取文件对象的 URL
	 * @return
	 */
	String getUrl();

	/**
	 * 获取文件对象的路径
	 * @return
	 */
	String getPath();

	/**
	 * 获取文件对象的全路径
	 * @return
	 */
	String getFullPath();

	/**
	 * 获取文件长度
	 * @return
	 */
	 long getSize();

	/**
	 * 以流形式打开文件对象
	 * @return
	 */
	 InputStream openStream();

	/**
	 * 把流数据写入文件对象
	 * @param stream
	 */
	 void write(InputStream stream);

	/**
	 * 把流数据写入文件对象，并指定文件对象的MIME
	 * @param stream
	 * @param mime
	 */
	 void write(InputStream stream, Mime mime);

	/**
	 * 把流数据写入文件对象，并指定写入长度
	 * @param stream
	 * @param writeLength
	 */
	 void write(InputStream stream, long writeLength);

	/**
	 * 把流数据写入文件对象，并指定写入长度和文件对象的MIME
	 * @param stream
	 * @param writeLength
	 * @param mime
	 */
	 void write(InputStream stream, long writeLength, Mime mime);

	/**
	 * 判断文件对象是否存在
	 * @return
	 */
	 boolean exists(String bucketName, String key);

	/**
	 * 删除文件对象
	 */
	 void delete();

}

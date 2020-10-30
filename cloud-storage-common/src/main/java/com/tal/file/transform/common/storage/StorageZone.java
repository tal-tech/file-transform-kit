package com.tal.file.transform.common.storage;


/**
 * Storage根对象
 * 
 * @author lazycathome
 *
 */
public interface StorageZone {

	/**
	 * 从根对象上创建文件对象
	 * @param urlOrPath
	 * @return
	 */
	StorageFile create(String urlOrPath);

	/**
	 * 从根对象上查找文件对象
	 * @param urlOrPath
	 * @return
	 */
	StorageFile lookup(String urlOrPath);

}

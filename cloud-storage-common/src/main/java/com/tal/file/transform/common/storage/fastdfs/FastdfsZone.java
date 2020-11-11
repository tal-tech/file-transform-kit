package com.tal.file.transform.common.storage.fastdfs;

import com.tal.file.transform.common.storage.StorageConfig;
import com.tal.file.transform.common.storage.StorageFile;
import com.tal.file.transform.common.storage.StorageZone;

/**
 * 存储根对象的 FastDfs 实现
 * 
 * @author lazycathome
 *
 */
public class FastdfsZone implements StorageZone {

	private StorageConfig conf = null;

	/**
	 * 静态工厂方法，简化调用关系
	 * @param conf
	 * @return
	 */
	public static StorageZone create(StorageConfig conf) {
		return new FastdfsZone(conf);
	}

	protected FastdfsZone(StorageConfig conf) {
		this.conf = conf;
	}

	@Override
	public StorageFile create(String urlOrPath) {
		return FastDfsFile.create(urlOrPath, conf, false);
	}

	@Override
	public StorageFile lookup(String urlOrPath) {
		return FastDfsFile.create(urlOrPath, conf, true);
	}

}

package com.tal.cloud.storage.common.storage.local;

import com.tal.cloud.storage.common.storage.StorageConfig;
import com.tal.cloud.storage.common.storage.StorageFile;
import com.tal.cloud.storage.common.storage.StorageZone;
import org.apache.log4j.Logger;

/**
 * 存储根对象的本地实现
 * 
 * @author lazycathome
 *
 */
public class LocalZone implements StorageZone {

	protected static final Logger log = Logger.getLogger(LocalZone.class);

	private StorageConfig conf = null;

	/**
	 * 静态工厂方法，简化调用关系
	 * @param conf
	 * @return
	 */
	public static StorageZone create(StorageConfig conf) {
		return new LocalZone(conf);
	}

	protected LocalZone(StorageConfig conf) {
		this.conf = conf;
	}

	@Override
	public StorageFile create(String urlOrPath) {
		return LocalFile.create(conf, urlOrPath, false);
	}

	@Override
	public StorageFile lookup(String urlOrPath) {
		return LocalFile.create(conf, urlOrPath, true);
	}


}

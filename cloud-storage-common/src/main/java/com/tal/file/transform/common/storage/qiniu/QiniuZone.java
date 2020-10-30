package com.tal.file.transform.common.storage.qiniu;

import com.qiniu.storage.Configuration;
import com.tal.file.transform.common.storage.StorageConfig;
import com.tal.file.transform.common.storage.StorageFile;
import com.tal.file.transform.common.storage.StorageZone;

import com.qiniu.storage.UploadManager;

/**
 * 存储根对象的 BOS 实现
 * 
 * @author lazycathome
 *
 */
public class QiniuZone implements StorageZone {

	private StorageConfig conf = null;
	private UploadManager uploadMgr = null;

	/**
	 * 静态工厂方法，简化调用关系
	 * @param conf
	 * @return
	 */
	public static StorageZone create(StorageConfig conf) {
		return new QiniuZone(conf);
	}

	protected QiniuZone(StorageConfig conf) {
		this.conf = conf;
		Configuration c = new Configuration();
		this.uploadMgr = new UploadManager(c);
	}

	@Override
	public StorageFile create(String urlOrPath) {
		return QiniuFile.create(uploadMgr, conf, urlOrPath, false);
	}

	@Override
	public StorageFile lookup(String urlOrPath) {
		return QiniuFile.create(uploadMgr, conf, urlOrPath, true);
	}

}

package com.tal.file.transform.common.storage.bos;

import com.tal.file.transform.common.storage.StorageConfig;
import com.tal.file.transform.common.storage.StorageFile;
import com.tal.file.transform.common.storage.StorageZone;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;

/**
 * 存储根对象的 BOS 实现
 * 
 * @author lazycathome
 *
 */
public class BosZone implements StorageZone, BosClientHandler {

	private StorageConfig conf = null;
	private BosClient client = null;

	/**
	 * 静态工厂方法，简化调用关系
	 * @param conf
	 * @return
	 */
	public static StorageZone create(StorageConfig conf) {
		return new BosZone(conf);
	}

	protected BosZone(StorageConfig conf) {
		this.conf = conf;

		reset();
	}

	@Override
	public StorageFile create(String urlOrPath) {
		return BosFile.create(this, conf, urlOrPath, false);
	}

	@Override
	public StorageFile lookup(String urlOrPath) {
		return BosFile.create(this, conf, urlOrPath, true);
	}

	@Override
	public BosClient getClient() {
		return client;
	}

	@Override
	public void reset() {
		BosClientConfiguration config = new BosClientConfiguration();

		config.setCredentials(new DefaultBceCredentials(conf.getAccessKey(), conf.getSecretKey()));

		this.client = new BosClient(config);
	}

}

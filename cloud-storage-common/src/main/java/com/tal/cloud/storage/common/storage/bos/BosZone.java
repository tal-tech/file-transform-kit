package com.tal.cloud.storage.common.storage.bos;

import com.tal.cloud.storage.common.storage.StorageConfig;
import com.tal.cloud.storage.common.storage.StorageFile;
import com.tal.cloud.storage.common.storage.StorageFileNamer;
import com.tal.cloud.storage.common.storage.StorageZone;
import com.tal.cloud.storage.common.storage.local.LocalZone;

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

	public static void main(String[] args) {
		StorageConfig bosConf = new StorageConfig.Builder()
							.setAccessKey("3d30addb6e214288ba811d960e1c0b02")
							.setSecretKey("451c8d16e52a4e6ca00794c5b5d776f5")
							.setEndpoint("http://bj.bcebos.com")
							.setBucket("gc-stor")
							.build();
		StorageZone bos = BosZone.create(bosConf);
		StorageFile bosFile = bos.create(new StorageFileNamer().bename("hello/world.jpg"));

		StorageConfig localConf = new StorageConfig.Builder()
								.setLocalPath("E:/lazycathome")
								.setPrefixUrl("")
								.build();
		StorageZone local = LocalZone.create(localConf);
		StorageFile localFile = local.lookup("/storages/jpg/20150703/4792ac267c498b2bfbb937f90d50bf02.jpg");

		bosFile.write(localFile.openStream());
	}

}

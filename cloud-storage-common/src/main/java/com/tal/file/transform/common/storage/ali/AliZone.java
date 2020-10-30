package com.tal.file.transform.common.storage.ali;

import com.tal.file.transform.common.storage.StorageConfig;
import com.tal.file.transform.common.storage.StorageFile;
import com.tal.file.transform.common.storage.StorageZone;

import com.aliyun.oss.OSSClient;

public class AliZone implements StorageZone {
	
	private StorageConfig conf = null;
	private OSSClient client = null;

	public static StorageZone create(StorageConfig conf) {
		return new AliZone(conf);
	}

	protected AliZone(StorageConfig conf) {
		this.conf = conf;
		this.client = new OSSClient(conf.getEndpoint(), conf.getAccessKey(), conf.getSecretKey());
	}

	@Override
	public StorageFile create(String urlOrPath) {
		return AliFile.create(client, conf, urlOrPath, false);
	}

	@Override
	public StorageFile lookup(String urlOrPath) {
		return AliFile.create(client, conf, urlOrPath, true);
	}

}

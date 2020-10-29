package com.tal.cloud.storage.common.storage.ali;

import java.io.IOException;

import com.tal.cloud.storage.common.storage.StorageConfig;
import com.tal.cloud.storage.common.storage.StorageFile;
import com.tal.cloud.storage.common.storage.StorageZone;
import com.tal.cloud.storage.common.storage.local.LocalZone;
import org.apache.commons.io.IOUtils;

import com.tal.cloud.storage.common.storage.bos.BosZone;

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

	public static void main(String[] args) throws IOException {
		StorageConfig aliConf = new StorageConfig.Builder()
							.setAccessKey("Oe9raa3EcnEcpPKf")
							.setSecretKey("GV9l39aXmXOK3LSKfGBYodZYKRfUBO")
							.setEndpoint("oss-cn-beijing.aliyuncs.com")
							.setPrefixUrl("http://s.zm518.cn/")
							.setBucket("gc-static")
							.build();
		StorageZone ali = AliZone.create(aliConf);
		StorageFile aliFile = ali.create("zmlive/电视台流地址分析.txt");
		StorageZone local = LocalZone.create(new StorageConfig.Builder().setLocalPath("C:/Users/lzm/Desktop").build());
		StorageFile localFile = local.lookup("电视台流地址分析.txt");

		aliFile.write(localFile.openStream());

		System.out.println(IOUtils.toString(aliFile.openStream()));

		StorageConfig bosConf = new StorageConfig.Builder()
							.setAccessKey("3d30addb6e214288ba811d960e1c0b02")
							.setSecretKey("451c8d16e52a4e6ca00794c5b5d776f5")
							.setEndpoint("http://bj.bcebos.com")
							.setPrefixUrl("http://gaiayhd.bj.bcebos.com/")
							.setBucket("gaiayhd")
							.build();
		StorageZone bos = BosZone.create(bosConf);
		StorageFile bosFile = bos.lookup("zmlive.test/0eea2adc81ecb4140bf6f68cae66a80e.m3u8");

		aliFile = ali.create("zmlive/test.m3u8");

		aliFile.write(bosFile.openStream());

		System.out.println(IOUtils.toString(aliFile.openStream()));
	}

}

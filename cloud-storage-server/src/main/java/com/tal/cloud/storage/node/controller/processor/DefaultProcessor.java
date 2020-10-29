package com.tal.cloud.storage.node.controller.processor;

import com.tal.cloud.storage.common.storage.StorageFileNamer;
import com.tal.cloud.storage.common.storage.StorageZone;
import com.tal.cloud.storage.node.controller.result.UploadResult;

import java.io.File;
import java.util.List;

public class DefaultProcessor implements UploadProcessor {

	private StorageFileNamer fileNamer = new StorageFileNamer();

	public static DefaultProcessor createDefaultProcessor() {
		return new DefaultProcessor();
	}

	@Override
	public UploadResult<?> process(String clientId, List<File> files, StorageZone zone) {
		return process(clientId, files, zone, fileNamer);
	}

	@Override
	public UploadResult<?> process(String clientId, List<File> files, StorageZone zone, StorageFileNamer fileNamer) {
		return null;
	}

}

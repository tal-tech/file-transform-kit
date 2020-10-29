package com.tal.cloud.storage.node.controller.processor;

import com.tal.cloud.storage.common.storage.StorageFileNamer;
import com.tal.cloud.storage.common.storage.StorageZone;
import com.tal.cloud.storage.node.controller.result.UploadResult;

import java.io.File;
import java.util.List;

public interface UploadProcessor {

	UploadResult<?> process(String clientId, List<File> files, StorageZone zone);
	UploadResult<?> process(String clientId, List<File> files, StorageZone zone, StorageFileNamer fileNamer);

}

package com.tal.file.transform.controller.processor;

import com.tal.file.transform.common.storage.StorageFileNamer;
import com.tal.file.transform.common.storage.StorageZone;
import com.tal.file.transform.controller.result.UploadResult;

import java.io.File;
import java.util.List;

public interface UploadProcessor {

	UploadResult<?> process(String clientId, List<File> files, StorageZone zone);
	UploadResult<?> process(String clientId, List<File> files, StorageZone zone, StorageFileNamer fileNamer);

}

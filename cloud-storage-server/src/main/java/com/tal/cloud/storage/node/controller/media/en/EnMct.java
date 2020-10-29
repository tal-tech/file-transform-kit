package com.tal.cloud.storage.node.controller.media.en;

import com.tal.cloud.storage.node.controller.media.Mct;

import com.tal.cloud.storage.common.storage.StorageFile;
import com.tal.cloud.storage.common.storage.StorageZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnMct implements Mct {

	protected static final Logger log = LoggerFactory.getLogger(EnMct.class);

	@Autowired
	StorageZone zone;

	@Autowired
	MctPipeline pipeline;

	@Autowired
	MctPresets presets;

	@Override
	public long getDuration(String storPath) {
		StorageFile file = zone.lookup(storPath);

		return pipeline.getDuration(file.getFullPath());
	}

	@Override
	public String transfer(String storPath, String pipeline, String preset, String mime) {
		StorageFile srcFile = zone.lookup(storPath);
		StorageFile targetFile = zone.create(storPath + Mct.KEY_TRANSFER);

		this.pipeline.transfer(srcFile.getFullPath(), targetFile.getFullPath(), preset, mime);

		return targetFile.getUrl();
	}

	@Override
	public String thumbnail(String storPath, String mime) {
		StorageFile srcFile = zone.lookup(storPath);
		StorageFile targetFile = zone.create(storPath + Mct.KEY_THUMBNAIL);

		pipeline.thumbnail(srcFile.getFullPath(), targetFile.getFullPath(), mime);

		return targetFile.getUrl();
	}

}

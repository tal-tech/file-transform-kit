package com.tal.file.transform.controller.media;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tal.file.transform.common.mime.MimeUtils;
import com.tal.file.transform.common.storage.StorageFile;
import com.tal.file.transform.common.storage.StorageZone;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MediaMetas {

	protected static final Logger log = Logger.getLogger(MediaMetas.class);

	@Autowired
    StorageZone zone;

	private Map<String, MediaMeta> metas = new HashMap<String, MediaMeta>();

	public void add(String storPath, MediaMeta meta) {
		metas.put(storPath, meta);
	}

	public void commit() {
		Gson gson = new Gson();

		for(Entry<String, MediaMeta> e : metas.entrySet()) {
			StorageFile metaFile = zone.create(e.getKey() + Mct.KEY_META);
			String metaData = gson.toJson(e.getValue());

			metaFile.write(IOUtils.toInputStream(metaData), MimeUtils.find(".json"));
		}
	}

}

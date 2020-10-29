package com.tal.cloud.storage.node.controller.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import com.tal.cloud.storage.common.mime.Mime;
import com.tal.cloud.storage.common.mime.MimeUtils;
import com.tal.cloud.storage.common.storage.StorageFile;
import com.tal.cloud.storage.common.storage.StorageZone;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageJobs {

	protected static final Logger log = LoggerFactory.getLogger(ImageJobs.class);

	private StorageZone zone = null;
	private Map<String, List<ImageJob>> jobs = new HashMap<String, List<ImageJob>>();

	public ImageJobs(StorageZone zone) {
		this.zone = zone;
	}

	public void add(BufferedImage img, String storPath, String bizName, Mime mime) {
		List<ImageJob> j = jobs.get(storPath);

		if(j == null) {
			j = new ArrayList<ImageJob>();
			
			jobs.put(storPath, j);
		}

		if(ImageCore.KEY_SELF.equals(bizName)) {
			j.add(new ImageJob(img, storPath, bizName, mime));

		} else {
			j.add(new ImageJob(img, storPath + bizName, bizName, mime));
		}
	}

	public void commit() {
		Gson gson = new Gson();

		for(Entry<String, List<ImageJob>> e : jobs.entrySet()) {
			Map<String, ImageSize> meta = new HashMap<String, ImageSize>();

			for(ImageJob j : e.getValue()) {
				BufferedImage img = j.getImg();

				save(img, j.getStorPath(), j.getMime());

				meta.put(j.getBizName(), new ImageSize(img.getWidth(), img.getHeight()));
			}

			StorageFile metaFile = zone.create(e.getKey() + ImageCore.KEY_META);
			String metaData = gson.toJson(meta);

			metaFile.write(IOUtils.toInputStream(metaData), MimeUtils.find(".json"));
		}
	}

	protected InputStream toInputStream(BufferedImage img, String imgType) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();  

			ImageIO.write(img, imgType.replace(".", ""), os);

			return new ByteArrayInputStream(os.toByteArray());

		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return null;
	}

	protected void save(BufferedImage img, String filePath, Mime mime) {
		long startTime = System.currentTimeMillis();
		InputStream stream = toInputStream(img, mime.getFileExt());
		StorageFile storFile = zone.create(filePath);

		log.info(String.format("   %s", filePath));

		storFile.write(stream, mime);

		log.info(String.format("// save cost: %d ms", System.currentTimeMillis() - startTime));
	}

}

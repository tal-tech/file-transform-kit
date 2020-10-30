package com.tal.file.transform.controller.processor;

import com.tal.file.transform.entity.Misc;
import com.tal.file.transform.common.FilenameUtils;
import com.tal.file.transform.common.mime.Mime;
import com.tal.file.transform.common.mime.MimeUtils;
import com.tal.file.transform.common.storage.StorageFile;
import com.tal.file.transform.common.storage.StorageFileNamer;
import com.tal.file.transform.common.storage.StorageZone;
import com.tal.file.transform.controller.result.UploadResult;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class MiscProcessor extends DefaultProcessor {

	protected static final Logger log = Logger.getLogger(MiscProcessor.class);

	@Override
	public UploadResult<Misc> process(String clientId, List<File> files, StorageZone zone, StorageFileNamer fileNamer) {
		UploadResult<Misc> r = new UploadResult<Misc>();

		for(int i = 0; i < files.size(); i ++) {
			File file = files.get(i);
			String fileName = file.getName();
			String filePath = fileNamer.bename(clientId + "/misc", fileName);
			StorageFile storFile = zone.create(filePath);

			log.info(String.format("   %s, %d bytes --> %s", fileName, file.length(), filePath));

			Misc misc = new Misc();

			misc.setName(fileName);
			misc.setUrl(storFile.getUrl());

			r.addFile(misc);

			Mime mime = MimeUtils.find(FilenameUtils.getExt(fileName));

			try {
				storFile.write(new FileInputStream(file), mime);

				file.delete();

			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}

		return r;
	}

}

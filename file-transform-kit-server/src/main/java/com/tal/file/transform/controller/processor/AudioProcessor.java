package com.tal.file.transform.controller.processor;

import com.tal.file.transform.controller.media.Mct;
import com.tal.file.transform.entity.Audio;
import com.tal.file.transform.media.MediaTask;
import com.tal.file.transform.media.MediaTasks;
import com.tal.file.transform.common.FilenameUtils;
import com.tal.file.transform.common.mime.Mime;
import com.tal.file.transform.common.mime.MimeUtils;
import com.tal.file.transform.common.storage.StorageFile;
import com.tal.file.transform.common.storage.StorageFileNamer;
import com.tal.file.transform.common.storage.StorageZone;
import com.tal.file.transform.controller.media.MediaCore;
import com.tal.file.transform.controller.media.MediaMeta;
import com.tal.file.transform.controller.media.MediaMetas;
import com.tal.file.transform.controller.result.UploadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class AudioProcessor extends DefaultProcessor {

	protected static final Logger log = LoggerFactory.getLogger(AudioProcessor.class);

	@Autowired
	MediaMetas metas;

	@Autowired
    MediaTasks mediaTasks;

	@Autowired
	Mct mct;

	@Override
	public UploadResult<Audio> process(String clientId, List<File> files, StorageZone zone, StorageFileNamer fileNamer) {
		UploadResult<Audio> r = new UploadResult<Audio>();
		List<MediaTask> tasks = mediaTasks.find(clientId);

		for(int i = 0; i < files.size(); i ++) {
			File file = files.get(i);
			String fileName = file.getName();
			String filePath = fileNamer.bename(clientId + "/audio", fileName);
			StorageFile storFile = zone.create(filePath);

			log.info(String.format("   %s, %d bytes --> %s", fileName, file.length(), filePath));

			Audio audio = new Audio();

			audio.setName(fileName);

			r.addFile(audio);

			Mime mime = MimeUtils.find(FilenameUtils.getExt(fileName));

			try {
				storFile.write(new FileInputStream(file), file.length(), mime);
				audio.setUrl(storFile.getUrl());
				file.delete();

			} catch (IOException e) {
				log.error(e.getMessage());
			}

			if(tasks != null) {
				for(MediaTask task : tasks) {
					if(MediaCore.getOp(task.getOperate()) == Mct.OPERATE_TRANSFER_AUDIO) {
						mct.transfer(filePath, task.getPipeline(), task.getPreset(), task.getMime());

						metas.add(filePath, new MediaMeta(mct.getDuration(filePath)));
					}
				}
			}
		}

		metas.commit();

		return r;
	}

}

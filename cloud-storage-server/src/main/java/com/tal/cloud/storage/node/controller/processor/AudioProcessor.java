package com.tal.cloud.storage.node.controller.processor;

import com.tal.cloud.storage.node.controller.media.Mct;
import com.tal.cloud.storage.node.entity.Audio;
import com.tal.cloud.storage.node.media.MediaTask;
import com.tal.cloud.storage.node.media.MediaTasks;
import com.tal.cloud.storage.common.FilenameUtils;
import com.tal.cloud.storage.common.mime.Mime;
import com.tal.cloud.storage.common.mime.MimeUtils;
import com.tal.cloud.storage.common.storage.StorageFile;
import com.tal.cloud.storage.common.storage.StorageFileNamer;
import com.tal.cloud.storage.common.storage.StorageZone;
import com.tal.cloud.storage.node.controller.media.MediaCore;
import com.tal.cloud.storage.node.controller.media.MediaMeta;
import com.tal.cloud.storage.node.controller.media.MediaMetas;
import com.tal.cloud.storage.node.controller.result.UploadResult;
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
			audio.setUrl(storFile.getUrl());

			r.addFile(audio);

			Mime mime = MimeUtils.find(FilenameUtils.getExt(fileName));

			try {
				storFile.write(new FileInputStream(file), mime);

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

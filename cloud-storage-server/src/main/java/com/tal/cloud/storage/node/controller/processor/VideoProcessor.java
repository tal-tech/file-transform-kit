package com.tal.cloud.storage.node.controller.processor;

import com.tal.cloud.storage.node.controller.media.Mct;
import com.tal.cloud.storage.node.entity.Video;
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
public class VideoProcessor extends DefaultProcessor {

	protected static final Logger log = LoggerFactory.getLogger(VideoProcessor.class);

	@Autowired
	MediaMetas metas;

	@Autowired
    MediaTasks mediaTasks;

	@Autowired
    Mct mct;

	@Override
	public UploadResult<Video> process(String clientId, List<File> files, StorageZone zone, StorageFileNamer fileNamer) {
		UploadResult<Video> r = new UploadResult<Video>();
		List<MediaTask> tasks = mediaTasks.find(clientId);

		for(int i = 0; i < files.size(); i ++) {
			File file = files.get(i);
			String fileName = file.getName();
			String filePath = fileNamer.bename(clientId + "/video", fileName);
			StorageFile storFile = zone.create(filePath);

			log.info("   %s, %d bytes --> %s", fileName, file.length(), filePath);

			Video video = new Video();

			video.setName(fileName);
			video.setUrl(storFile.getUrl());

			r.addFile(video);

			Mime mime = MimeUtils.find(FilenameUtils.getExt(fileName));

			try {
				storFile.write(new FileInputStream(file), mime);

				file.delete();

			} catch (IOException e) {
				log.error(e.getMessage());
			}

			if(tasks != null) {
				for(MediaTask task : tasks) {
					switch(MediaCore.getOp(task.getOperate())) {
					case Mct.OPERATE_TRANSFER_VIDEO:
						mct.transfer(filePath, task.getPipeline(), task.getPreset(), task.getMime());

						metas.add(filePath, new MediaMeta(mct.getDuration(filePath)));
						break;

					case Mct.OPERATE_THUMBNAIL:
						mct.thumbnail(filePath, task.getMime());
						break;
					}
				}
			}
		}

		metas.commit();

		return r;
	}

}

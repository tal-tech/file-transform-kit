package com.tal.cloud.storage.node.controller.processor;

import com.tal.cloud.storage.node.controller.image.ImageCore;
import com.tal.cloud.storage.node.controller.image.ImageJobs;
import com.tal.cloud.storage.node.controller.image.ImageSize;
import com.tal.cloud.storage.node.entity.Image;
import com.tal.cloud.storage.common.FilenameUtils;
import com.tal.cloud.storage.common.StringUtils;
import com.tal.cloud.storage.common.mime.Mime;
import com.tal.cloud.storage.common.mime.MimeUtils;
import com.tal.cloud.storage.common.storage.StorageFile;
import com.tal.cloud.storage.common.storage.StorageFileNamer;
import com.tal.cloud.storage.common.storage.StorageZone;
import com.tal.cloud.storage.node.controller.result.UploadResult;
import com.tal.cloud.storage.node.image.ImageTask;
import com.tal.cloud.storage.node.image.ImageTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ImageProcessor extends DefaultProcessor {

	protected static final Logger log = LoggerFactory.getLogger(ImageProcessor.class);

	private final static int JOB_OPTIMIZE = 1;
	private final static int JOB_SCALE = 2;
	private final static int JOB_SPECIFIC = 4;
	private final static int JOB_ALL = JOB_OPTIMIZE | JOB_SCALE | JOB_SPECIFIC;
	private final static int JOB_ALL_WITHOUT_OPTIMIZE = JOB_SCALE | JOB_SPECIFIC;
	private final static int JOB_SAVE_ONLY = 8;

	@Autowired
    ImageTasks imageTasks;

	@Override
	public UploadResult<Image> process(String clientId, List<File> files, StorageZone zone, StorageFileNamer fileNamer) {
		UploadResult<Image> r = new UploadResult<Image>();
		ImageJobs jobs = new ImageJobs(zone);
		Map<String, String> fileNames = new HashMap<String, String>();
		List<ImageTask> tasks = imageTasks.find(clientId);

		for(int i = 0; i < files.size(); i ++) {
			File file = files.get(i);
			// liujt, 2015.12.8
			// 鉴于png的压缩率不高的原因，强制将png转换成jpeg格式，使最终生成的文件更小，确保下载速度
			// 注：这个修改主要是因为掌门iOS版上传直播大列表图片时，png文件过大导致直播大列表打开太慢
			//String fileName = file.getName().replace(".png", ".jpeg");
			String fileName = file.getName();
			String fileExts = FilenameUtils.getExt(fileName);

			String filePath = "", bizName = "";
			int jobType = JOB_ALL;

			// liujt, 2019.5.19
			// 当上传的图片没有后缀时，给定默认后缀名
			if(StringUtils.isBlank(fileExts)) {
				fileExts = ".jpeg";
				fileName += fileExts;
			}

            if (imageTasks.findPaths(clientId) || Objects.isNull(fileNamer.getFilePath())) {
                filePath = fileNamer.bename(clientId + "/image", fileName);
            } else {
			    filePath = fileNamer.getFilePath() + fileName;
            }

			StorageFile storFile = zone.create(filePath);
			Image image = new Image();

			image.setName(fileName);
			image.setUrl(storFile.getUrl());

			r.addFile(image);

			filePath = storFile.getPath();

			BufferedImage img = null;

			try {
				img = ImageIO.read(new FileInputStream(file));

			} catch (IOException e) {
				log.error(e.getMessage());
			}

			Mime mime = MimeUtils.find(fileExts);

			switch(jobType) {
			case JOB_ALL:
			case JOB_ALL_WITHOUT_OPTIMIZE:

				// 默认保存自身
				jobs.add(img, filePath, ImageCore.KEY_SELF, mime);

				if(tasks != null) {
					ImageCore ic = new ImageCore();

					for(ImageTask task : tasks) {
						int w = img.getWidth(), h = img.getHeight();
						BufferedImage t = null;

						switch(ImageCore.getOp(task.getOperate())) {
						case ImageCore.OPERATE_OPTIMIZE:
							bizName = ImageCore.KEY_OPTIMIZE;

							if((jobType & JOB_OPTIMIZE) == JOB_OPTIMIZE) {
								ImageSize limit = task.getLimitSize();

								if(limit != null && limit.getWidth() > 0 && limit.getHeight() > 0) {
									t = ic.optimize(img, w, h, limit.getWidth(), limit.getHeight());
								} else {
									t = ic.optimize(img);
								}

							} else {
								t = img;
							}
							break;

						case ImageCore.OPERATE_SCALE:
							int minW = task.getMinSize().getWidth(), minH = task.getMinSize().getHeight();
							int maxW = task.getMaxSize().getWidth(), maxH = task.getMaxSize().getHeight();

							bizName = String.format(ImageCore.KEY_SCALE, minW, minH, maxW, maxH);

							t = ic.scale(img, w, h, minW, minH, maxW, maxH);
							break;
						case ImageCore.OPERATE_SELF_SCALE:
							int selfW = task.getSize().getWidth(), selfH = task.getSize().getHeight();

							bizName = String.format(ImageCore.KEY_SELF, selfW, selfH);

							t = ic.specific(img, w, h, selfW, selfH);
							break;
						case ImageCore.OPERATE_SPECIFIC:
							int specW = task.getSize().getWidth(), specH = task.getSize().getHeight();

							bizName = String.format(ImageCore.KEY_SPECIFIC, specW, specH);

							t = ic.specific(img, w, h, specW, specH);
							break;

						}

						jobs.add(t, filePath, bizName, mime);

					}
				}

				break;

			case JOB_SAVE_ONLY:
				jobs.add(img, filePath, bizName, mime);

				ImageCore ic = new ImageCore();

				// liujt, 2015.8.11
				// 根据缩略图生成更小尺寸的缩略图
				Pattern specP = Pattern.compile("_r(\\d+)x(\\d+)$");
				Matcher specM = specP.matcher(bizName);

				if(specM.find() && tasks != null) {
					int w = StringUtils.toInt(specM.group(1)), h = StringUtils.toInt(specM.group(2));
					double scale = (double)w / (double)h;

					for(ImageTask task : tasks) {
						if(ImageCore.getOp(task.getOperate()) == ImageCore.OPERATE_SPECIFIC) {
							int specW = task.getSize().getWidth(), specH = task.getSize().getHeight();
							double specScale = (double)specW / (double)specH;

							if(scale == specScale && specW < w && specH < h) {
								BufferedImage bi = ic.specific(img, specW, specH);

								bizName = String.format(ImageCore.KEY_SPECIFIC, specW, specH);

								jobs.add(bi, filePath, bizName, mime);
							}
						}
					}
				}
				break;
			}
		}

		jobs.commit();

		return r;
	}

}

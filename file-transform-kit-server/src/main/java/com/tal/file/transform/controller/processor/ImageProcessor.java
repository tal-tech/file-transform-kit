package com.tal.file.transform.controller.processor;

import com.tal.file.transform.entity.Audio;
import com.tal.file.transform.entity.Image;
import com.tal.file.transform.image.ImageTask;
import com.tal.file.transform.image.ImageTasks;
import com.tal.file.transform.controller.image.ImageCore;
import com.tal.file.transform.controller.image.ImageJobs;
import com.tal.file.transform.controller.image.ImageSize;
import com.tal.file.transform.common.FilenameUtils;
import com.tal.file.transform.common.StringUtils;
import com.tal.file.transform.common.mime.Mime;
import com.tal.file.transform.common.mime.MimeUtils;
import com.tal.file.transform.common.storage.StorageFile;
import com.tal.file.transform.common.storage.StorageFileNamer;
import com.tal.file.transform.common.storage.StorageZone;
import com.tal.file.transform.controller.result.UploadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

		List<ImageTask> tasks = imageTasks.find(clientId);

		for(int i = 0; i < files.size(); i ++) {
			File file = files.get(i);
			// liujt, 2020.8.8
			// 鉴于png的压缩率不高的原因，强制将png转换成jpeg格式，使最终生成的文件更小，确保下载速度
			// 注：这个修改主要是因为掌门iOS版上传直播大列表图片时，png文件过大导致直播大列表打开太慢

			String fileName = file.getName();
			String fileExts = FilenameUtils.getExt(fileName);

			String filePath = "", bizName = "";
			int jobType = JOB_ALL;

			// liujt, 2020.5.19
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

			Image image = new Image();
			image.setName(fileName);
			r.addFile(image);

			BufferedImage img = null;
			Mime mime = MimeUtils.find(fileExts);
			StorageFile storFile = zone.create(filePath);

			try {
				InputStream inputStream = new FileInputStream(file);
				img = ImageIO.read(inputStream);
				storFile.write(inputStream, mime);
				image.setUrl(storFile.getUrl());

				file.delete();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
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
						default:
							break;
						}

						jobs.add(t, filePath, bizName, mime);

					}
				}

				break;
			case JOB_SAVE_ONLY:
				jobs.add(img, filePath, bizName, mime);

				ImageCore ic = new ImageCore();

				// liujt, 2020.8.11
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
			default:
				break;
			}
		}

		jobs.commit();

		return r;
	}

}

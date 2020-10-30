package com.tal.file.transform.common.storage.ali;

import java.io.IOException;
import java.io.InputStream;

import com.tal.file.transform.common.FilenameUtils;
import com.tal.file.transform.common.StringUtils;
import com.tal.file.transform.common.mime.Mime;
import com.tal.file.transform.common.mime.MimeUtils;
import com.tal.file.transform.common.storage.StorageConfig;
import com.tal.file.transform.common.storage.StorageFile;
import com.tal.file.transform.common.storage.bos.BosFile;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AliFile implements StorageFile {

	protected static final Logger log = LoggerFactory.getLogger(BosFile.class);

	private OSSClient ossClient = null;
	private StorageConfig conf = null;
	private String path = "";
	private boolean lookup = false;

	/**
	 * 静态工厂方法，简化调用关系
	 * @param ossClient
	 * @param conf
	 * @param path
	 * @param beLookup
	 * @return
	 */
	public static StorageFile create(OSSClient ossClient, StorageConfig conf, String path, boolean beLookup) {
		return new AliFile(ossClient, conf, path, beLookup);
	}

	protected AliFile(OSSClient ossClient, StorageConfig conf, String path, boolean beLookup) {
		this.ossClient = ossClient;
		this.conf = conf;
		this.path = path;
		this.lookup = beLookup;
	}

	@Override
	public String getUrl() {
		if(lookup && StringUtils.isUrl(path)) {
			return path;
		}

		return String.format("%s%s", conf.getPrefixUrl(), path);
	}

	@Override
	public String getPath() {
		if(StringUtils.isUrl(path)) {
			String prefixUrl = conf.getPrefixUrl();

			if(path.startsWith(prefixUrl)) {
				return path.substring(prefixUrl.length());
			}

			return "";
		}

		return path;
	}

	@Override
	public String getFullPath() {
		return getUrl();
	}

	@Override
	public long getSize() {
		String path = getPath();

		if(StringUtils.isNotBlank(path)) {
			OSSObject oo = ossClient.getObject(conf.getBucket(), path);

			return oo.getObjectMetadata().getContentLength();
		}

		return 0;
	}

	@Override
	public InputStream openStream() {
		String path = getPath();

		if(StringUtils.isNotBlank(path)) {
			OSSObject oo = ossClient.getObject(conf.getBucket(), path);

			return oo.getObjectContent();
		}
	    
		return null;
	}

	@Override
	public void write(InputStream stream) {
		write(stream, MimeUtils.find(FilenameUtils.getExt(getPath())));
	}

	@Override
	public void write(InputStream stream, Mime mime) {
	    try {
	    	long length = stream.available();

	    	write(stream, length, mime);

	    } catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void write(InputStream stream, long writeLength) {
		write(stream, writeLength, MimeUtils.find(FilenameUtils.getExt(getPath())));
	}

	@Override
	public void write(InputStream stream, long writeLength, Mime mime) {
	    // 创建上传Object的Metadata
	    ObjectMetadata meta = new ObjectMetadata();

	    // 必须设置ContentLength
		meta.setContentLength(writeLength);
	    meta.setContentType(mime.getMimeType());

	    // 上传Object.
	    // lazycathome, 2019.6.6
	    // Ali OSS 不允许第一个字符为 / 或 \
	    PutObjectResult result = ossClient.putObject(conf.getBucket(), path.replaceAll("^/+", ""), stream, meta);
	}

	@Override
	public boolean exists(String bucketName, String key) {
		return ossClient.doesObjectExist(bucketName, key);
	}

	@Override
	public void delete() {
		
	}

}

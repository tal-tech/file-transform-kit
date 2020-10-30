package com.tal.file.transform.common.storage.bos;

import java.io.InputStream;

import com.tal.file.transform.common.FilenameUtils;
import com.tal.file.transform.common.StringUtils;
import com.tal.file.transform.common.mime.Mime;
import com.tal.file.transform.common.mime.MimeUtils;
import com.tal.file.transform.common.storage.StorageConfig;
import com.tal.file.transform.common.storage.StorageFile;
import org.apache.log4j.Logger;

import com.baidubce.BceClientException;
import com.baidubce.BceServiceException;
import com.baidubce.services.bos.model.BosObject;
import com.baidubce.services.bos.model.ObjectMetadata;

/**
 * 文件对象的百度 BOS 实现
 * 
 * @author lazycathome
 *
 */
public class BosFile implements StorageFile {

	protected static final Logger log = Logger.getLogger(BosFile.class);

	private BosClientHandler bos = null;
	private StorageConfig conf = null;
	private String path = "";
	private boolean lookup = false;

	/**
	 * 静态工厂方法，简化调用关系
	 * @param client
	 * @param conf
	 * @param path
	 * @param beLookup
	 * @return
	 */
	public static StorageFile create(BosClientHandler bos, StorageConfig conf, String path, boolean beLookup) {
		return new BosFile(bos, conf, path, beLookup);
	}

	protected BosFile(BosClientHandler bos, StorageConfig conf, String path, boolean beLookup) {
		this.bos = bos;
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
			BosObject bo = bos.getClient().getObject(conf.getBucket(), path);

			return bo.getObjectMetadata().getContentLength();
		}

		return 0;
	}

	@Override
	public InputStream openStream() {
		String path = getPath();

		if(StringUtils.isNotBlank(path)) {
			BosObject bo = bos.getClient().getObject(conf.getBucket(), path);

			return bo.getObjectContent();
		}

		return null;
	}

	@Override
	public void write(InputStream stream) {
		write(stream, MimeUtils.find(FilenameUtils.getExt(getPath())));
	}

	@Override
	public void write(InputStream stream, Mime mime) {
		String path = getPath();

		if(StringUtils.isNotBlank(path)) {
			boolean result = upload(path, stream, mime);
			int tryCount = 0;

			while(!result && tryCount < 3) {
				bos.reset();

				result = upload(path, stream, mime);

				tryCount ++;
			}
		}
	}

	@Override
	public void write(InputStream stream, long writeLength) {
		write(stream);
	}

	@Override
	public void write(InputStream stream, long writeLength, Mime mime) {
		write(stream, mime);
	}

	protected boolean upload(String fileName, InputStream stream, Mime mime) {
		try {
			ObjectMetadata meta = new ObjectMetadata();

			meta.setContentType(mime.getMimeType());

			bos.getClient().putObject(conf.getBucket(), fileName, stream, meta);

			return true;

		} catch (BceServiceException e){
			log.error(e.getMessage());

		} catch (BceClientException e){
			log.error(e.getMessage());
		}

		return false;
	}

	@Override
	public boolean exists(String bucketName, String key) {
		return true;
	}

	@Override
	public void delete() {
		
	}

}

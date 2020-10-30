package com.tal.file.transform.common.storage.qiniu;

import java.io.IOException;
import java.io.InputStream;

import com.tal.file.transform.common.FilenameUtils;
import com.tal.file.transform.common.mime.Mime;
import com.tal.file.transform.common.mime.MimeUtils;
import com.tal.file.transform.common.StringUtils;
import com.tal.file.transform.common.storage.StorageConfig;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.tal.file.transform.common.storage.StorageFile;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * 文件对象的百度 BOS 实现
 * 
 * @author lazycathome
 *
 */
public class QiniuFile implements StorageFile {

	protected static final Logger log = Logger.getLogger(QiniuFile.class);

	private UploadManager uploadMgr = null;
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
	public static StorageFile create(UploadManager uploadMgr, StorageConfig conf, String path, boolean beLookup) {
		return new QiniuFile(uploadMgr, conf, path, beLookup);
	}

	protected QiniuFile(UploadManager uploadMgr, StorageConfig conf, String path, boolean beLookup) {
		this.uploadMgr = uploadMgr;
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
		String url = getUrl();

		if(StringUtils.isNotBlank(url) && StringUtils.isUrl(url)) {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(url).method("head", null).build();

			try {
				com.squareup.okhttp.Response response = client.newCall(request).execute();

				if (response.isSuccessful()) {
					return StringUtils.toLong(response.header("Content-Length"));
				}

			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}

		return 0;
	}

	@Override
	public InputStream openStream() {
		String url = getUrl();

		if(StringUtils.isNotBlank(url) && StringUtils.isUrl(url)) {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(url).build();

			try {
				com.squareup.okhttp.Response response = client.newCall(request).execute();

				if (response.isSuccessful()) {
					return response.body().byteStream();
				}

			} catch (IOException e) {
				log.error(e.getMessage());
			}
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
		Auth auth = Auth.create(conf.getAccessKey(), conf.getSecretKey());
		String token = auth.uploadToken(conf.getBucket());

		try {
			Response res = uploadMgr.put(IOUtils.toByteArray(stream), fileName, token, null, mime.getMimeType(), false);

			return res.isOK();

		} catch (QiniuException e) {
			log.error(e.getMessage());

		} catch (IOException e) {
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

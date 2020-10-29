package com.tal.cloud.storage.common.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tal.cloud.storage.common.FilenameUtils;
import com.tal.cloud.storage.common.mime.Mime;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.tal.cloud.storage.common.StringUtils;
import com.tal.cloud.storage.common.storage.StorageConfig;
import com.tal.cloud.storage.common.storage.StorageFile;


/**
 * 文件对象的本地实现
 * 
 * @author lazycathome
 *
 */
public class LocalFile implements StorageFile {

	protected static final Logger log = Logger.getLogger(LocalFile.class);

	private StorageConfig conf = null;
	private String path = "";
	private boolean lookup = false;

	/**
	 * 静态工厂方法，简化调用关系
	 * @param conf
	 * @param path
	 * @param beLookup
	 * @return
	 */
	public static StorageFile create(StorageConfig conf, String path, boolean beLookup) {
		return new LocalFile(conf, path, beLookup);
	}

	protected LocalFile(StorageConfig conf, String path, boolean beLookup) {
		this.conf = conf;
		this.path = path;
		this.lookup = beLookup;
	}

	@Override
	public String getUrl() {
		if(lookup && StringUtils.isUrl(path)) {
			return path;
		}

		return String.format("%s/%s", conf.getPrefixUrl(), path).replaceAll("[\\\\/]+", "/").replaceFirst(":/", "://");
	}

	@Override
	public String getPath() {
		if(StringUtils.isUrl(path)) {
			String starts = conf.getPrefixUrl();

			if(path.startsWith(starts)) {
				return path.substring(starts.length()).replaceAll("[\\\\/]+", "/");
			}

			return "";
		}

		String starts = conf.getLocalPath();

		if(path.startsWith(starts)) {
			return path.substring(starts.length()).replaceAll("[\\\\/]+", "/");
		}

		return path.replaceAll("[\\\\/]+", "/");
	}

	@Override
	public String getFullPath() {
		if(StringUtils.isUrl(path)) {
			String starts = conf.getPrefixUrl();

			if(path.startsWith(starts)) {
				return String.format("%s/%s", conf.getLocalPath(), path.substring(starts.length())).replaceAll("[\\\\/]+", "/");
			}

			return "";
		}

		String starts = conf.getLocalPath();

		if(path.startsWith(starts)) {
			return path.replaceAll("[\\\\/]+", "/");
		}

		return String.format("%s/%s", starts, path).replaceAll("[\\\\/]+", "/");
	}

	@Override
	public long getSize() {
		File file = new File(getFullPath());
	
		return file.length();
	}

	@Override
	public InputStream openStream() {
		try {
			String path = getFullPath();

			return new FileInputStream(path);

		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		}

		return null;
	}

	@Override
	public void write(InputStream stream) {
		try {
			String filename = getFullPath();
			String path = FilenameUtils.getPath(filename);

			FileUtils.forceMkdir(new File(path));

			FileOutputStream output = new FileOutputStream(filename);

	    	IOUtils.copy(stream, output);

	    	output.close();

		} catch (FileNotFoundException e) {
			log.error(e.getMessage());

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void write(InputStream stream, Mime mime) {
		write(stream);
	}

	@Override
	public void write(InputStream stream, long writeLength) {
		write(stream);
	}

	@Override
	public void write(InputStream stream, long writeLength, Mime mime) {
		write(stream);
	}

	@Override
	public boolean exists(String bucketName, String key) {
		String path = getFullPath();
		File file = new File(path);

		return file.exists() && file.isFile();
	}

	@Override
	public void delete() {
		String path = getFullPath();
		File file = new File(path);

		if(file.exists() && file.isFile()) {
			file.delete();
		}
	}

}

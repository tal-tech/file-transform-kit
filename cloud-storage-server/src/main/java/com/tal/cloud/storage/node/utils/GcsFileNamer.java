package com.tal.cloud.storage.node.utils;

import java.util.Date;

import com.tal.cloud.storage.common.FilenameUtils;
import com.tal.cloud.storage.common.storage.StorageFileNamer;

public class GcsFileNamer extends StorageFileNamer {

	private final static int MAX_NO = 100000;

	private int lastMin = -1;
	private int lastNo = 1;

	@Override
	public String bename(String prefix, String origFilename) {
		Date now = new Date();
		String ext = FilenameUtils.getExt(origFilename);
		int min = now.getMinutes();

		if(lastMin != min || lastNo >= MAX_NO) {
			lastMin = min;
			lastNo = 1;
		}

		return String.format("/%s/%tY%tm%td/%tH/%tM%tS.%05d%s", prefix, now, now, now, now, now, now, lastNo ++, ext).toLowerCase();
	}

	public static void main(String[] args) {
		GcsFileNamer namer = new GcsFileNamer();

		for(long i = 0; i < 100100; i ++) {
			System.out.println(namer.bename("hello/image", "shit1.jpg"));
		}
	}
}

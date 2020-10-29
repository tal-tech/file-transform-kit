package com.tal.cloud.storage.common;


/**
 * 文件名工具类
 * 
 * @author lazycathome
 *
 */
public class FilenameUtils {

	/**
	 * 从指定的全路径文件名中，获取文件所在的文件夹路径
	 * @param path
	 * @return
	 */
	public static String getPath(String path) {
		String t = path.replaceAll("[\\\\/]+", "/").trim();
		int n = t.lastIndexOf("/");

		if(n != -1) {
			return t.substring(0, n);
		}

		return "";
	}

	/**
	 * 从指定的全路径文件名中，获取文件名
	 * @param path
	 * @return
	 */
	public static String getName(String path) {
		String t = path.replaceAll("[\\\\/]+", "/").trim();
		int n = t.lastIndexOf("/");

		if(n != -1) {
			return t.substring(n + 1);
		}

		return "";
	}

	/**
	 * 从指定的全路径文件名中，获取文件扩展名
	 * @param path
	 * @return
	 */
	public static String getExt(String path) {
		String t = path.replaceAll("[\\\\/]+", "/").trim();
		int n = t.lastIndexOf(".");

		if(n != -1) {
			int m = t.lastIndexOf("/");

			if(m > n) {
				return "";
			}

			String r = t.substring(n).trim();

			if(r.length() > 12) {
				return r.substring(0, 12);
			}

			return r;
		}

		return "";
	}

	public static void main(String[] args) {
		String s = "f:/asdfasdf\\sdfasd.xsdd/wrt";

		System.out.println(FilenameUtils.getExt(s));
	}

}

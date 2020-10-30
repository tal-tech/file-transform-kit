package com.tal.file.transform.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 加密工具类
 * 
 * @author lazycathome
 *
 */
public class EncryptUtils {

	private static final Logger log = LoggerFactory.getLogger(EncryptUtils.class);

	private static final String MD5 = "MD5";
	private static final String SHA = "SHA";

	/**
	 * 指定算法加密byte数组
	 * @param source
	 * @param algorithm
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] encrypt(byte[] source, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);

		md.reset();
		md.update(source);

		return md.digest();
	}

	/**
	 * 指定算法加密字符串
	 * @param source
	 * @param algorithm
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String encrypt(String source, String algorithm) throws NoSuchAlgorithmException {
		byte[] b = encrypt(source.getBytes(), algorithm);

		return StringUtils.toHexString(b);
	}

	/**
	 * MD5签名
	 * @param source
	 * @return
	 */
	public static String Md5(String source) {
		if(StringUtils.isNotBlank(source)) {
			try {
				return encrypt(source, MD5);

			} catch (NoSuchAlgorithmException e) {
				log.error(e.getMessage());
			}
		}

		return "";
	}

	/**
	 * SHA加密
	 * @param source
	 * @return
	 */
	public static String Sha(String source) {
		if(StringUtils.isNotBlank(source)) {
			try {
				return encrypt(source, SHA);

			} catch (NoSuchAlgorithmException e) {
				log.error(e.getMessage());
			}
		}

		return "";
	}

}
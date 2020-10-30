package com.tal.file.transform.common;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串工具类
 * 
 * @author lazycathome
 *
 */
public class StringUtils {

	private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

	/**
	 * 判断字符串是否为空(包括null)
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if(str != null) {
			return "".equals(str.trim());
		}

		return true;
	}

	/**
	 * 判断字符串是否非空(包括null)
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * 将字符串转换成布尔型
	 * @param bool
	 * @return
	 */
	public static boolean toBool(String bool) {
		return toBool(bool, false);
	}

	/**
	 * 将字符串转换成布尔型，转换失败时，返回给定的默认值
	 * @param bool
	 * @param def
	 * @return
	 */
	public static boolean toBool(String bool, boolean def) {
		if(StringUtils.isNotBlank(bool)) {
			String t = bool.trim();

			return ("checked".equalsIgnoreCase(t) ||
					"enabled".equalsIgnoreCase(t) ||
					"enable".equalsIgnoreCase(t) ||
					"true".equalsIgnoreCase(t) ||
					"yes".equalsIgnoreCase(t) ||
					"on".equalsIgnoreCase(t) ||
					"1".equalsIgnoreCase(t));
		}

		return def;
	}

	/**
	 * 将字符串转换成数字
	 * @param num
	 * @return
	 */
	public static int toInt(String num) {
		return toInt(num, 0);
	}

	/**
	 * 将字符串转换成数字，转换失败时，返回给定的默认值
	 * @param num
	 * @param def
	 * @return
	 */
	public static int toInt(String num, int def) {
		try {
			if(StringUtils.isNotBlank(num)) {
				return Integer.valueOf(num.trim());
			}

		} catch(NumberFormatException e) {
			log.error(String.format("toInt(%s) failed: ", num), e);
		}

		return def;
	}

	/**
	 * 将字符串转换成数字
	 * @param num
	 * @return
	 */
	public static long toLong(String num) {
		return toLong(num, 0);
	}

	/**
	 * 将字符串转换成数字，转换失败时，返回给定的默认值
	 * @param num
	 * @param def
	 * @return
	 */
	public static long toLong(String num, long def) {
		try {
			if(StringUtils.isNotBlank(num)) {
				return Long.valueOf(num.trim());
			}

		} catch(NumberFormatException e) {
			log.error(String.format("toLong(%s) failed: ", num), e);
		}

		return def;
	}

	/**
	 * 将字符串转换成数字
	 * @param num
	 * @return
	 */
	public static float toFloat(String num) {
		return toFloat(num, 0);
	}

	/**
	 * 将字符串转换成数字，转换失败时，返回给定的默认值
	 * @param num
	 * @param def
	 * @return
	 */
	public static float toFloat(String num, float def) {
		try {
			if(StringUtils.isNotBlank(num)) {
				BigDecimal db = new BigDecimal(num.trim());

				return db.floatValue();
			}

		} catch(NumberFormatException e) {
			log.error(String.format("toFloat(%s) failed: ", num), e);
		}

		return def;
	}

	/**
	 * 将字符串转换成数字
	 * @param num
	 * @return
	 */
	public static double toDouble(String num) {
		return toDouble(num, 0);
	}

	/**
	 * 将字符串转换成数字，转换失败时，返回给定的默认值
	 * @param num
	 * @param def
	 * @return
	 */
	public static double toDouble(String num, double def) {
		try {
			if(StringUtils.isNotBlank(num)) {
				BigDecimal db = new BigDecimal(num.trim());

				return db.doubleValue();
			}

		} catch(NumberFormatException e) {
			log.error(String.format("toDouble(%s) failed: ", num), e);
		}

		return def;
	}

	/**
	 * 将字符串转换成日期
	 * @param date
	 * @return
	 */
	public static Date toDate(String date, String fmt) {
		return toDate(date, fmt, new Date());
	}

	/**
	 * 将字符串转换成日期
	 * @param date
	 * @return
	 */
	public static Date toDate(String date, String fmt, Date d) {
        SimpleDateFormat df = new SimpleDateFormat(fmt.trim());

        try {
        	return df.parse(date.trim());

        } catch (ParseException e) {
			log.error(String.format("toDate(%s, %s) failed: ", date, fmt), e);
		}

        return d;
	}

	/**
	 * 将日期转换成字符串
	 * @param date
	 * @param fmt
	 * @return
	 */
	public static String fromDate(Date date, String fmt) {
        SimpleDateFormat df = new SimpleDateFormat(fmt);

        return df.format(date);
	}

	/**
	 * 把字符串解析成List<Long>
	 * @param str
	 * @return
	 */
	public static List<Long> toIdList(String str) {
		return toIdList(str, ",");
	}

	/**
	 * 把字符串根据指定的分隔符号分割，并解析成List<Long>
	 * @param str
	 * @param token
	 * @return
	 */
	public static List<Long> toIdList(String str, String token) {
		List<Long> r = new ArrayList<Long>();

		if(StringUtils.isNotBlank(str)) {
			String[] ids = str.split(token);

			if(ids != null) {
				for(String id : ids) {
					r.add(toLong(id, 0));
				}
			}
		}

		return r;
	}

	/**
	 * 把字符串解析成List<String>
	 * @param str
	 * @return
	 */
	public static List<String> toList(String str) {
		return toList(str, ",");
	}

	/**
	 * 把字符串根据指定的分隔符号分割，并解析成List<String>
	 * @param str
	 * @param token
	 * @return
	 */
	public static List<String> toList(String str, String token) {
		List<String> r = new ArrayList<String>();

		if(StringUtils.isNotBlank(str)) {
			String[] ids = str.split(token);

			if(ids != null) {
				for(String id : ids) {
					r.add(id.trim());
				}
			}
		}

		return r;
	}

	/**
	 * 判断字符串是否是URL
	 * @param url
	 * @return
	 */
	public static boolean isUrl(String url) {
		return (url.startsWith("http://") || url.startsWith("https://"));
	}

	/**
	 * 把byte数组转换成十六进制的字符串
	 * @param b
	 * @return
	 */
	public static String toHexString(byte[] b) {
		StringBuilder str = new StringBuilder();

		for(int i = 0; i < b.length; i ++) {
			String hex = Integer.toHexString(b[i] & 0xFF);

			if(hex.length() == 1) {
				str.append("0");
		    }

			str.append(hex);
		  } 

		return str.toString();
	}

}

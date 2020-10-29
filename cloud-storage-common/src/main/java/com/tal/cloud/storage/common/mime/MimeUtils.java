package com.tal.cloud.storage.common.mime;

import com.tal.cloud.storage.common.ConfigUtils;
import com.tal.cloud.storage.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * MIME工具类
 * 主要用于通过文件扩展名和MIME类型之间的相互查询
 * 
 * @author lazycathome
 *
 */
public class MimeUtils {

	private static final Logger log = LoggerFactory.getLogger(MimeUtils.class);

	private static final String JSON_FE2M_DEFAULT = "default.fe2m.json";
	private static final String JSON_M2FE_DEFAULT = "default.m2fe.json";

	private static Map<String, String> exts = null;
	private static Map<String, String> mimes = null;

	/**
	 * 查找MIME
	 * @param mimeOrExt
	 * @return
	 */
	public static synchronized Mime find(String mimeOrExt) {
		if(StringUtils.isNotBlank(mimeOrExt)) {
			prepare();

			String me = mimeOrExt.toLowerCase().trim();

			if(me.indexOf("/") != -1) {
				if(mimes.containsKey(me)) {
					return new Mime(mimes.get(me), me);
				}

			} else if(exts.containsKey(me)) {
				return new Mime(me, exts.get(me));
			}
		}

		return new Mime();
	}

	/**
	 * 准备数据
	 */
	@SuppressWarnings("unchecked")
	public static void prepare() {
		if(exts == null || mimes == null) {
			exts = ConfigUtils.load(Map.class, Mime.class.getResourceAsStream(JSON_FE2M_DEFAULT), ConfigUtils.TYPE_JSON);
			mimes = ConfigUtils.load(Map.class, Mime.class.getResourceAsStream(JSON_M2FE_DEFAULT), ConfigUtils.TYPE_JSON);
		}
	}

	/**
	 * 反转 MAP：key -> value，value -> key
	 * @param m
	 * @return
	 */
	protected static Map<String, String> inverseMap(Map<String, String> m) {
		if(m != null) {
			Map<String, String> r = new HashMap<String, String>();

			for(Entry<String, String> e : m.entrySet()) {
				r.put(e.getValue(), e.getKey());
			}

			return r;
		}

		return null;
	}

	public static void main(String[] args) {
		Mime m = MimeUtils.find("image/gif");

		System.out.println(m.getFileExt());
	}

}

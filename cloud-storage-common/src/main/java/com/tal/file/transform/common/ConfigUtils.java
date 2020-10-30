package com.tal.file.transform.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * 配置信息加载工具类
 * 支持对properties文件、JSON文件，以及XML文件进行对象化，即把一个配置文件映射成一个简单实体对象
 * 
 * @author lazycathome
 *
 */
public class ConfigUtils {

	private static final Logger log = Logger.getLogger(ConfigUtils.class);

	public final static int TYPE_PROPERTIES = 0;
	public final static int TYPE_JSON = 1;
	public final static int TYPE_XML = 2;

	private static Gson gson = new Gson();

	/**
	 * 把一个properties流映射成一个指定的实体对象
	 * @param clazz 实体对象的class
	 * @param stream 
	 * @return
	 */
	public static synchronized <T> T load(Class<T> clazz, InputStream stream) {
		return _loadFromFile(clazz, stream, TYPE_PROPERTIES);
	}

	/**
	 * 把流映射成一个指定的实体对象，文件格式由type决定
	 * @param clazz 实体对象的class
	 * @param stream
	 * @param type 
	 * @return
	 */
	public static synchronized <T> T load(Class<T> clazz, InputStream stream, int type) {
		return _loadFromFile(clazz, stream, type);
	}

	/**
	 * 把一个properties流映射到一个指定的实体对象
	 * @param entity
	 * @param stream
	 * @return
	 */
	public static synchronized <T> T load(T entity, InputStream stream) {
		return _loadFromFile(entity, stream, TYPE_PROPERTIES);
	}

	/**
	 * 把流映射到一个指定的实体对象，文件格式由type决定
	 * @param entity
	 * @param stream
	 * @param type
	 * @return
	 */
	public static synchronized <T> T load(T entity, InputStream stream, int type) {
		return _loadFromFile(entity, stream, type);
	}

	/**
	 * 把一个properties文件映射成一个指定的实体对象
	 * @param clazz
	 * @param filename
	 * @return
	 */
	public static synchronized <T> T loadFromFile(Class<T> clazz, String filename) {
		return _loadFromFile(clazz, filename, TYPE_PROPERTIES);
	}

	/**
	 * 把文件映射成一个指定的实体对象，文件格式由type决定
	 * @param clazz
	 * @param filename
	 * @param type
	 * @return
	 */
	public static synchronized <T> T loadFromFile(Class<T> clazz, String filename, int type) {
		return _loadFromFile(clazz, filename, type);
	}

	/**
	 * 把一个properties文件映射到一个指定的实体对象
	 * @param entity
	 * @param filename
	 * @return
	 */
	public static synchronized <T> T loadFromFile(T entity, String filename) {
		return _loadFromFile(entity, filename, TYPE_PROPERTIES);
	}

	/**
	 * 把文件映射到一个指定的实体对象
	 * @param entity
	 * @param filename
	 * @param type
	 * @return
	 */
	public static synchronized <T> T loadFromFile(T entity, String filename, int type) {
		return _loadFromFile(entity, filename, type);
	}

	/**
	 * 把流转换成对象
	 * @param clazz
	 * @param stream
	 * @param type
	 * @return
	 */
	protected static <T> T _loadFromFile(Class<T> clazz, InputStream stream, int type) {
		switch(type) {
		case TYPE_PROPERTIES: return loadFromProperties(clazz, stream);
		case TYPE_JSON: return loadFromJSON(clazz, stream);
		case TYPE_XML: break;
		}

		return null;
	}

	/**
	 * 把流映射到对象，并覆盖对象的现有属性值
	 * @param entity
	 * @param stream
	 * @param type
	 * @return
	 */
	protected static <T> T _loadFromFile(T entity, InputStream stream, int type) {
		switch(type) {
		case TYPE_PROPERTIES: return loadFromProperties(entity, stream);
		case TYPE_JSON:
		case TYPE_XML: break;
		}

		return null;
	}

	/**
	 * 把文件转换成对象
	 * @param clazz
	 * @param filename
	 * @param type
	 * @return
	 */
	protected static <T> T _loadFromFile(Class<T> clazz, String filename, int type) {
		switch(type) {
		case TYPE_PROPERTIES: return loadFromProperties(clazz, filename);
		case TYPE_JSON: return loadFromJSON(clazz, filename);
		case TYPE_XML: break;
		}

		return null;
	}

	/**
	 * 把文件映射到对象，并覆盖对象的现有属性值
	 * @param entity
	 * @param filename
	 * @param type
	 * @return
	 */
	protected static <T> T _loadFromFile(T entity, String filename, int type) {
		switch(type) {
		case TYPE_PROPERTIES: return loadFromProperties(entity, filename);
		case TYPE_JSON:
		case TYPE_XML: break;
		}

		return null;
	}

	/**
	 * 加载properties流，并返回实体对象
	 * @param clazz
	 * @param stream
	 * @return
	 */
	protected static <T> T loadFromProperties(Class<T> clazz, InputStream stream) {
		return loadFromProperties(ClassUtils.createInstance(clazz), stream);
	}

	/**
	 * 加载properties流，映射实体对象属性值
	 * @param entity
	 * @param stream
	 * @return
	 */
	protected static <T> T loadFromProperties(T entity, InputStream stream) {
		if(entity != null) {
			Properties props = openProperties(stream);

			if(props != null) {
				Map<String, String> fieldValues = buildFieldValues(props);

				ClassUtils.setFieldValues(entity, fieldValues);
			}

			return entity;
		}

		return null;
	}

	/**
	 * 加载properties文件，并返回实体对象
	 * @param clazz
	 * @param filename
	 * @return
	 */
	protected static <T> T loadFromProperties(Class<T> clazz, String filename) {
		return loadFromProperties(ClassUtils.createInstance(clazz), filename);
	}

	/**
	 * 加载properties文件，映射实体对象属性值
	 * @param entity
	 * @param filename
	 * @return
	 */
	protected static <T> T loadFromProperties(T entity, String filename) {
		try {
			FileInputStream fis = new FileInputStream(new File(filename));

			return loadFromProperties(entity, fis);

		} catch (FileNotFoundException e) {
			log.error(String.format("loadFromProperties(%s) failed: ", filename), e);
		}

		return null;
	}

	/**
	 * 以properties方式打开流
	 * @param stream
	 * @return
	 */
	protected static Properties openProperties(InputStream stream) {
		if(stream != null) {
			try {
				Properties props = new Properties();

				props.load(stream);

				return props;

			} catch (IOException e) {
				log.error("openProperties() failed: ", e);
			}
		}

		return null;
	}

	/**
	 * 以properties方式打开文件
	 * @param stream
	 * @return
	 */
	protected static Properties openProperties(String filename) {
		try {
			FileInputStream stream = new FileInputStream(new File(filename));

			return openProperties(stream);

		} catch (FileNotFoundException e) {
			log.error(String.format("openProperties(%s) failed: ", filename), e);
		}

		return null;
	}

	/**
	 * 根据properties生成属性/值列表
	 * @param props
	 * @return
	 */
	protected static Map<String, String> buildFieldValues(Properties props) {
		Map<String, String> fieldValues = new HashMap<String, String>();

		for(Object key : props.keySet()) {
			String kn = key.toString();

			fieldValues.put(formatFieldName(kn), props.getProperty(kn, ""));
		}

		return fieldValues;
	}


	/**
	 * 格式化属性名，配置文件中允许以"."、"_"和"-"等符号记录属性值
	 * 该方法会以这三个符号对配置文件中的属性名进行分割，格式化成java的驼峰式命名，以对应实体类的属性名
	 * @param name
	 * @return
	 */
	protected static String formatFieldName(String name) {
		StringBuffer fieldName = new StringBuffer();
		String[] names = name.split("[\\._-]+");

		fieldName.append(names[0].trim());

		for(int i = 1; i < names.length; i ++) {
			String n = names[i].trim();

			if(n.length() > 0) {
				fieldName.append(n.substring(0, 1).toUpperCase());
				fieldName.append(n.substring(1));
			}
		}

		return fieldName.toString();
	}

	/**
	 * 把JSON格式的文本流映射成实体对象
	 * @param clazz
	 * @param stream
	 * @return
	 */
	protected static <T> T loadFromJSON(Class<T> clazz, InputStream stream) {
		if(stream != null) {
			InputStreamReader reader = new InputStreamReader(stream);

			return gson.fromJson(reader, clazz);
		}

		return null;
	}

	/**
	 * 把JSON文件流映射成实体对象
	 * @param clazz
	 * @param filename
	 * @return
	 */
	protected static <T> T loadFromJSON(Class<T> clazz, String filename) {
		try {
			FileInputStream fis = new FileInputStream(new File(filename));

			return loadFromJSON(clazz, fis);

		} catch (FileNotFoundException e) {
			log.error(String.format("loadFromJSON(%s) failed: ", filename), e);
		}

		return null;
	}

}

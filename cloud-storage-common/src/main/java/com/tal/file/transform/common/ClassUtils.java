package com.tal.file.transform.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * 类操作相关工具类
 * 
 * @author lazycathome
 *
 */
public class ClassUtils {

	private static final Logger log = Logger.getLogger(ClassUtils.class);

	/**
	 * 根据class创建实体类
	 * @param clazz
	 * @return
	 */
	public static <T> T createInstance(Class<T> clazz) {
		try {
			if(clazz != null) {
				return clazz.newInstance();
			}

		} catch (InstantiationException e) {
			log.error(String.format("createInstance(%s) failed: ", clazz.getName()), e);

		} catch (IllegalAccessException e) {
			log.error(String.format("createInstance(%s) failed: ", clazz.getName()), e);
		}

		return null;
	}

	public static <T> Map<String, String> getFieldValues(T entity) {
		Map<String, String> fieldValues = new HashMap<String, String>();
		Field[] fields = entity.getClass().getDeclaredFields();

		for(Field field : fields) {
			String fieldName = field.getName();
			String fieldValue = valueToString(entity, field);

			fieldValues.put(fieldName, fieldValue);
		}

		return fieldValues;
	}

	protected static <T> String valueToString(T entity, Field field) {
		field.setAccessible(true);

		try {
			String type = field.getGenericType().toString();

			if("class java.lang.String".equals(type)) {
				return (String)field.get(entity);

			} else if("int".equals(type) || "class java.lang.Integer".equals(type)) {
				return String.format("%d", field.getInt(entity));

			} else if("long".equals(type) || "class java.lang.Long".equals(type)) {
				return String.format("%d", field.getLong(entity));

			} else if("float".equals(type) || "class java.lang.Float".equals(type)) {
				return String.format("%.02f", field.getFloat(entity));

			} else if("double".equals(type) || "class java.lang.Double".equals(type)) {
				return String.format("%.02f", field.getDouble(entity));

			} else if("boolean".equals(type) || "class java.lang.Boolean".equals(type)) {
				return field.getBoolean(entity) ? "true" : "false";

			} else {
				log.warn(String.format("getFieldValue(%s) not supported \"%s\".", field.getName(), type));
			}

		} catch (IllegalArgumentException e) {
			log.error(String.format("getFieldValue(%s) failed: ", field.getName()), e);

		} catch (IllegalAccessException e) {
			log.error(String.format("getFieldValue(%s) failed: ", field.getName()), e);
		}

		return "";
	}

	/**
	 * 通过反射，把属性/值列表映射到实体对象
	 * @param entity
	 * @param fieldValues
	 */
	public static <T> void setFieldValues(T entity, Map<String, String> fieldValues) {
		Field[] fields = entity.getClass().getDeclaredFields();

		for(Field field : fields) {
			String fieldName = field.getName();

			if(fieldValues.containsKey(fieldName)) {
				String value = fieldValues.get(fieldName);

				if(!setFieldValue(entity, field, value)) {
					setFieldValue(entity, fieldName, value);
				}
			}
		}
	}

	/**
	 * 通过反射，为实体对象的属性赋值
	 * @param entity
	 * @param field
	 * @param value
	 */
	protected static <T> boolean setFieldValue(T entity, Field field, String value) {
		field.setAccessible(true);

		try {
			String type = field.getGenericType().toString();

//			log.info(String.format("%s %s = %s;", type, field.getName(), value));
//
			if("class java.lang.String".equals(type)) {
				field.set(entity, value);

			} else if("int".equals(type) || "class java.lang.Integer".equals(type)) {
				field.set(entity, StringUtils.toInt(value));

			} else if("long".equals(type) || "class java.lang.Long".equals(type)) {
				field.set(entity, StringUtils.toLong(value));

			} else if("float".equals(type) || "class java.lang.Float".equals(type)) {
				field.set(entity, StringUtils.toFloat(value));

			} else if("double".equals(type) || "class java.lang.Double".equals(type)) {
				field.set(entity, StringUtils.toDouble(value));

			} else if("boolean".equals(type) || "class java.lang.Boolean".equals(type)) {
				field.set(entity, StringUtils.toBool(value));

			} else {
				log.warn(String.format("setFieldValue(%s, %s) not supported \"%s\".", field.getName(), value, type));
			}

			return true;

		} catch (IllegalArgumentException e) {
			log.error(String.format("setFieldValue(%s, %s) failed: ", field.getName(), value), e);

		} catch (IllegalAccessException e) {
			log.error(String.format("setFieldValue(%s, %s) failed: ", field.getName(), value), e);
		}

		return false;
	}

	protected static <T> boolean setFieldValue(T entity, String fieldName, String value) {
		String methodName = String.format("set%s%s", fieldName.substring(0, 1).toUpperCase(), fieldName.substring(1)); 
		Method[] methods = entity.getClass().getDeclaredMethods();

		for(Method method : methods) {
			if(methodName.equals(method.getName())) {
				Class<?>[] ts = method.getParameterTypes();

				if(ts.length == 1) {
					List<String> os = new ArrayList<String>();
					String type = ts[0].getName();

//					log.info(String.format("%s(%s = %s).", methodName, type, value));
//
					if("java.lang.String".equals(type)) {
						os.add(value);

/*					} else if("int".equals(type) || "java.lang.Integer".equals(type)) {
						os.add(StringUtils.toInt(value));

					} else if("long".equals(type) || "java.lang.Long".equals(type)) {
						os.add(StringUtils.toLong(value));

					} else if("float".equals(type) || "java.lang.Float".equals(type)) {
						os.add(StringUtils.toFloat(value));

					} else if("double".equals(type) || "java.lang.Double".equals(type)) {
						os.add(StringUtils.toDouble(value));

					} else if("boolean".equals(type) || "java.lang.Boolean".equals(type)) {
						os.add(StringUtils.toBool(value));

*/					} else {
						log.warn(String.format("%s(%s) not supported \"%s\".", methodName, value, type));
					}

					try {
						if(os.size() > 0) {
							method.invoke(entity, os.toArray());

							return true;
						}

					} catch (IllegalAccessException e) {
						log.error(String.format("%s(%s) failed: ", methodName, value), e);

					} catch (IllegalArgumentException e) {
						log.error(String.format("%s(%s) failed: ", methodName, value), e);

					} catch (InvocationTargetException e) {
						log.error(e.getMessage());
					}
				}
			}
		}

		return false;
	}

	public static boolean setFieldValue(Object entity, Field field, Object value) {
		field.setAccessible(true);

		try {
			field.set(entity, value);

			return true;

		} catch (IllegalArgumentException e) {
			log.error(String.format("setFieldValue(%s) failed: ", field.getName()), e);

		} catch (IllegalAccessException e) {
			log.error(String.format("setFieldValue(%s) failed: ", field.getName()), e);
		}

		return false;
	}

    /**
	 * 通过包名进行扫描
	 * @param packName
	 * @return
	 */
	public static Set<Class<?>> scan(String packName) {
		return scan(packName, true);
	}

	/**
	 * 通过包名进行扫描，并指定是否开启深度扫描（即是否扫描子目录）
	 * @param packName
	 * @param recursive
	 * @return
	 */
	public static Set<Class<?>> scan(String packName, boolean recursive) {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();

		String packageName = packName;
		String packageDirName = packageName.replace('.', '/');

		Enumeration<URL> dirs;

		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();

				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");

					scan(packageName, filePath, recursive, classes);

				} else if ("jar".equals(protocol)) {
					scanInJar(packageName, url, recursive, classes);
				}
			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return classes;
	}

	/**
	 * 通过包名在文件系统扫描
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	private static void scan(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
		File dir = new File(packagePath);

		if (!dir.exists() || !dir.isDirectory()) {
			log.warn("Invalid classpath: " + packageName + ".");

			return;
		}

		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});

		for (File file : dirfiles) {
			if (file.isDirectory()) {
				scan(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);

			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);

				try {
					classes.add(Class.forName(packageName + '.' + className));

				} catch (ClassNotFoundException e) {
					log.error(e.getMessage());
				}
			}
		}
	}

	/**
	 * 通过包名，在jar包中进行扫描
	 * @param packageName
	 * @param url
	 * @param recursive
	 * @param classes
	 */
	private static void scanInJar(String packageName, URL url, final boolean recursive, Set<Class<?>> classes) {
		String packageDirName = packageName.replace('.', '/');

		try {
			JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();

				if (name.charAt(0) == '/') {
					name = name.substring(1);
				}

				if (name.startsWith(packageDirName)) {
					int idx = name.lastIndexOf('/');

					if (idx != -1) {
						packageName = name.substring(0, idx).replace('/', '.');
					}

					if ((idx != -1) || recursive) {
						if (name.endsWith(".class") && !entry.isDirectory()) {
							String className = name.substring(packageName.length() + 1, name.length() - 6);

							try {
								classes.add(Class.forName(packageName + '.' + className));

							} catch (ClassNotFoundException e) {
								log.error(e.getMessage());
							}
						}
					}
				}
			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

}

package com.tal.cloud.storage.common.storage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 存储对象工厂
 * @author lazycathome
 *
 */
public class StorageFactory {

	private static final Logger log = LoggerFactory.getLogger(StorageFactory.class);

	/**
	 * 目前支持的三种存储对象
	 * @author lazycathome
	 *
	 */
	public enum StorageProvider {
		local("本地存储"), bos("百度BOS"), qiniu("七牛云存储"), ali("阿里云");

		StorageProvider(String name) { }
	}

	/**
	 * 获取存储空间对象
	 * @param provider	存储空间提供者（即StorageProvider所定义的枚举类型）
	 * @param conf		存储配置信息
	 * @return
	 */
	public static StorageZone getZone(StorageProvider provider, StorageConfig conf) {
		String p = provider.toString();
		String pn = p.substring(0, 1).toUpperCase() + p.substring(1).toLowerCase();

		try {
			Class<?> clazz = Class.forName(String.format("com.xes.dtc.common.storage.%s.%sZone", pn.toLowerCase(), pn));

			Method method = clazz.getMethod("create", new Class[] { StorageConfig.class });
			Object[] params = new StorageConfig[] { conf };

	        return (StorageZone)method.invoke(null, params);

		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());

		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());

		} catch (SecurityException e) {
			log.error(e.getMessage());

		} catch (IllegalAccessException e) {
			log.error(e.getMessage());

		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());

		} catch (InvocationTargetException e) {
			log.error(e.getTargetException().getMessage());
		}

		return null;
	}

	public static void main(String[] args) {
		StorageProvider p = StorageProvider.valueOf("local");
		StorageZone zone = StorageFactory.getZone(p, new StorageConfig.Builder().setLocalPath("d:/").setPrefixUrl("").build());
		System.out.println(p.toString());
		StorageFile file = zone.lookup("lazycathome.txt");
		file.write(IOUtils.toInputStream("shit"));
		System.out.println(file.getFullPath());
		StorageZone z2 = StorageFactory.getZone(StorageProvider.valueOf("qiniu"), new StorageConfig.Builder().setAccessKey("4vpkqRrvMiXIxHbS1uGxef5t5P4zL3kpk8t2xM84").setSecretKey("ZnOBT4FXfeKSCq5bbSrgVCMNzti8u2ay1vicKXr6").setBucket("gaiay-apps").setPrefixUrl("http://gaiay-apps.qiniudn.com/").build());
		StorageZone ali = StorageFactory.getZone(StorageProvider.ali, new StorageConfig.Builder()
																												.setAccessKey("aco3dPIKzQu4B9D4")
																												.setSecretKey("1c7JBt2JpNK1I26meC8MYtR7qoDFc1")
																												.setEndpoint("oss-cn-beijing.aliyuncs.com")
																												.setPrefixUrl("http://livetest.oss-cn-beijing.aliyuncs.com/")
																												.setBucket("livetest")
																												.build());
		StorageFile f2 = z2.create(file.getPath());
		f2.write(file.openStream());
		StorageFile fa = ali.create(file.getPath());
		fa.write(file.openStream(), file.getSize());
		try {
			String t = IOUtils.toString(f2.openStream());
			System.out.println(t);
			
			String t2 = IOUtils.toString(fa.openStream());
			System.out.println(t2);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

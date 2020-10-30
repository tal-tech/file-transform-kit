package com.tal.file.transform.common.rest.client;

import java.io.InputStream;

import com.tal.file.transform.common.ConfigUtils;
import org.apache.log4j.Logger;


/**
 * RESTful 客户端工具类
 * 
 * @author lazycathome
 *
 */
public class RestClients {

	private static final Logger log = Logger.getLogger(RestClients.class);

	private static final String CONF_DEFAULT = "default.properties";
	private static final String CONF_CUSTOMS = "rest-client.properties";
	
	private static RestConfig conf = null;

	/**
	 * 根据配置信息创建RestClient
	 * 
	 * @param conf
	 * @return
	 */
	public static synchronized RestClient create(RestConfig conf) {
		return new RestClient(conf);
	}

	/**
	 * 根据默认的配置信息，创建RestClient
	 * 
	 * @return
	 */
	public static synchronized RestClient createDefault() {
		prepare();

		return new RestClient(conf);
	}

	/**
	 * 初始化默认配置信息
	 */
	protected static void prepare() {
		if(conf == null) {
			conf = ConfigUtils.load(RestConfig.class, RestConfig.class.getResourceAsStream(CONF_DEFAULT));

			ConfigUtils.load(conf, RestConfig.class.getClassLoader().getResourceAsStream(CONF_CUSTOMS));
		}
	}

	public static void main(String[] args) {
/*		RestRequest request = new RestRequest("http://www.baidu.com/");
		request.addParam("wd", "擦啊");
		request.addFile("fuck", "C:/Users/pengms/Desktop/部署项目使用说明.txt");
		RestResponse<String> response = RestClients.createDefault().get(request, String.class);
		System.out.println(response.getHttpCode() + ": " + response.getResult());
*/
		RestClient client = RestClients.createDefault();
		RestRequest request = new RestRequest("http://gcsnode.gaiay.cn/gc-stor/ae32c42e64434648af5cccb25ee1e906/image/png/20151022/23/3314.92f1a7a8068b6daf451ad94fe04e3deb.png");
		RestResponse<InputStream> response = client.get(request, InputStream.class);
		response.getResult();
	}

}

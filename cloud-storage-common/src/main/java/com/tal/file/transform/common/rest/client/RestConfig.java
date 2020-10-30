package com.tal.file.transform.common.rest.client;


/**
 * RESTClient的配置信息类
 * 
 * @author lazycathome
 *
 */
public class RestConfig {

	private static final String DEFAULT_USER_AGENT = "xes-en/0.1 RestClient/0.1";

	private int socketTimeout = 10000;
	private int connectTimeout = 10000;
	private String userAgent = DEFAULT_USER_AGENT;

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}

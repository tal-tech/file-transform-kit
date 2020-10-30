package com.tal.file.transform.common.rest.client;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tal.file.transform.common.StringUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RESTful 客户端请求包装类
 * 
 * @author lazycathome
 *
 */
public class RestRequest {

	private static final Logger log = LoggerFactory.getLogger(RestRequest.class);

	public static final int METHOD_PUT = 1;
	public static final int METHOD_GET = 2;
	public static final int METHOD_POST = 3;
	public static final int METHOD_DELETE = 4;

	private String url = "";
	private Map<String, String> headers = new HashMap<String, String>();
	private List<NameValuePair> params = new ArrayList<NameValuePair>();
	private List<NameValuePair> files = new ArrayList<NameValuePair>();

	public RestRequest() {

	}

	public RestRequest(String url) {
		this.url = url;
	}

	/**
	 * 设置请求url
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 添加请求头
	 * @param headerName
	 * @param headerValue
	 */
	public void addHeader(String headerName, String headerValue) {
		headers.put(headerName, headerValue);
	}

	/**
	 * 添加请求参数
	 * @param paramName
	 * @param paramValue
	 */
	public void addParam(String paramName, String paramValue) {
		params.add(new BasicNameValuePair(paramName, paramValue));
	}

	/**
	 * 添加上传文件
	 * @param paramName
	 * @param filename
	 */
	public void addFile(String paramName, String filename) {
		files.add(new BasicNameValuePair(paramName, filename));
	}

	/**
	 * 创建HTTP请求
	 * @param httpMethod
	 * @param conf
	 * @return
	 */
	public HttpUriRequest createHttpRequest(int httpMethod, RestConfig conf) {
		HttpRequestBase request = null;

		if(StringUtils.isNotBlank(url)) {
			switch(httpMethod) {
			case METHOD_PUT: request = new HttpPut(url); break;

			case METHOD_GET:
			case METHOD_DELETE:
				String queryString = URLEncodedUtils.format(params, HTTP.UTF_8);

				if(StringUtils.isNotBlank(queryString)) {
					if(url.indexOf("?") != -1) {
						url += ("&" + queryString);

					} else {
						url += ("?" + queryString);
					}
				}

				if(httpMethod == METHOD_GET) {
					request = new HttpGet(url);

					break;
				}

				request = new HttpDelete(url);
				break;

			case METHOD_POST: request = new HttpPost(url); break;
			}

			setGlobalConfigs(request, conf);

			setHeaders(request);
			setParams(request, httpMethod);

		} else {
			log.error("Invalid url.");
		}

		return request;
	}

	/**
	 * 设置全局配置信息
	 * @param request
	 * @param conf
	 */
	protected void setGlobalConfigs(HttpRequestBase request, RestConfig conf) {
		if(conf.getConnectTimeout() != -1 || conf.getSocketTimeout() != -1) {
			Builder builder = RequestConfig.custom();

			if(conf.getConnectTimeout() != -1) {
				builder.setConnectTimeout(conf.getConnectTimeout());
			}

			if(conf.getSocketTimeout() != -1) {
				builder.setSocketTimeout(conf.getSocketTimeout());
			}

			request.setConfig(builder.build());
		}

		request.addHeader("User-Agent", conf.getUserAgent());
	}

	/**
	 * 设置请求头
	 * @param request
	 */
	protected void setHeaders(HttpUriRequest request) {
		for(Entry<String, String> entry : headers.entrySet()) {
			request.setHeader(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 设置请求参数
	 * @param request
	 * @param httpMethod
	 */
	protected void setParams(HttpUriRequest request, int httpMethod) {
		switch(httpMethod) {
		case METHOD_PUT:
			HttpPut put = (HttpPut)request;

			try {
				put.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			} catch (UnsupportedEncodingException e) {
				log.error("PUT.setParams() failed: ", e);
			}
			break;

		case METHOD_GET: break;

		case METHOD_POST:
			HttpPost post = (HttpPost)request;

			if(files.size() > 0) {
				MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	
				for(NameValuePair param : files) {
					builder.addPart(param.getName(), new FileBody(new File(param.getValue())));
				}

				for(NameValuePair param : params) {
					builder.addPart(param.getName(), new StringBody(param.getValue(), ContentType.create("text/plain", Consts.UTF_8)));
				}

				builder.setCharset(Consts.UTF_8);

				post.setEntity(builder.build());

			} else {
				try {
					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

				} catch (UnsupportedEncodingException e) {
					log.error("POST.setParams() failed: ", e);
				}
			}
			break;

		case METHOD_DELETE: break;
		}
	}

}

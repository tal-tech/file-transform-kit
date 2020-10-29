package com.tal.cloud.storage.common.rest.client;

import java.io.IOException;

import com.tal.cloud.storage.common.rest.ApiResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RESTful 客户端封装类
 * 
 * @author lazycathome
 *
 */
public class RestClient {

	private static final Logger log = LoggerFactory.getLogger(RestClient.class);

	private RestConfig conf = null;
	private CloseableHttpClient httpClient = null;

	public RestClient(RestConfig conf) {
		this.conf = conf;
		this.httpClient = HttpClients.createDefault();
	}

	/**
	 * 以GET方式执行REST请求，返回标准回应实体类
	 * @param request
	 * @return
	 */
	public RestResponse<ApiResult> get(RestRequest request) {
		return execute(request, RestRequest.METHOD_GET, ApiResult.class);
	}

	/**
	 * 以GET方式执行REST请求，并指定返回结果中的映射实体类
	 * @param request
	 * @param clazz
	 * @return
	 */
	public <T> RestResponse<T> get(RestRequest request, Class<T> clazz) {
		return execute(request, RestRequest.METHOD_GET, clazz);
	}

	/**
	 * 以PUT方式执行REST请求，返回标准回应实体类
	 * @param request
	 * @return
	 */
	public RestResponse<ApiResult> put(RestRequest request) {
		return execute(request, RestRequest.METHOD_PUT, ApiResult.class);
	}

	/**
	 * 以PUT方式执行REST请求，并指定返回结果中的映射实体类
	 * @param request
	 * @param clazz
	 * @return
	 */
	public <T> RestResponse<T> put(RestRequest request, Class<T> clazz) {
		return execute(request, RestRequest.METHOD_PUT, clazz);
	}

	/**
	 * 以POST方式执行REST请求，返回标准回应实体类
	 * @param request
	 * @return
	 */
	public RestResponse<ApiResult> post(RestRequest request) {
		return execute(request, RestRequest.METHOD_POST, ApiResult.class);
	}

	/**
	 * 以POST方式执行REST请求，并指定返回结果中的映射实体类
	 * @param request
	 * @param clazz
	 * @return
	 */
	public <T> RestResponse<T> post(RestRequest request, Class<T> clazz) {
		return execute(request, RestRequest.METHOD_POST, clazz);
	}

	/**
	 * 以DELETE方式执行REST请求，返回标准回应实体类
	 * @param request
	 * @return
	 */
	public RestResponse<ApiResult> delete(RestRequest request) {
		return execute(request, RestRequest.METHOD_DELETE, ApiResult.class);
	}

	/**
	 * 以DELETE方式执行REST请求，并指定返回结果中的映射实体类
	 * @param request
	 * @param clazz
	 * @return
	 */
	public <T> RestResponse<T> delete(RestRequest request, Class<T> clazz) {
		return execute(request, RestRequest.METHOD_DELETE, clazz);
	}

	/**
	 * 以指定的方式执行REST请求，并指定返回结果中的映射实体类
	 * @param httpMethod
	 * @param request
	 * @param clazz
	 * @return
	 */
	protected <T> RestResponse<T> execute(RestRequest request, int httpMethod, Class<T> clazz) {
		HttpUriRequest method = request.createHttpRequest(httpMethod, conf);

		return new RestResponse<T>(_execute(method), clazz);
	}

	/**
	 * 执行HTTP请求
	 * @param request
	 * @return
	 */
	protected HttpResponse _execute(HttpUriRequest request) {
		CloseableHttpResponse response = null;

		if(httpClient != null && request != null) {
			try {
				response = httpClient.execute(request);

			} catch (ClientProtocolException e) {
				log.error(String.format("%S \"%s\" failed: ", request.getMethod(), request.getURI()), e);

			} catch (IOException e) {
				log.error(String.format("%S \"%s\" failed: ", request.getMethod(), request.getURI()), e);
			}
		}

		return response;
	}

}

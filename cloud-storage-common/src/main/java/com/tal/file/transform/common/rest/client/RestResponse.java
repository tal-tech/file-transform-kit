package com.tal.file.transform.common.rest.client;

import java.io.IOException;

import com.tal.file.transform.common.ClassUtils;
import com.tal.file.transform.common.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RESTful 服务器应答包装类
 * 
 * @author lazycathome
 *
 * @param <T>
 */
public class RestResponse<T> {

	private static final Logger log = LoggerFactory.getLogger(RestResponse.class);

	private int httpCode = 0;
	private T data = null;

	public RestResponse(HttpResponse response, Class<T> clazz) {
		setHttpResponse(response, clazz);
	}

	/**
	 * 获取HTTP返回值
	 * @return
	 */
	public int getHttpCode() {
		return httpCode;
	}

	/**
	 * 获取服务器返回结果
	 * @return
	 */
	public T getResult() {
		return data;
	}

	/**
	 * 解析HTTP返回
	 * @param response
	 * @param clazz
	 */
	@SuppressWarnings("unchecked")
	protected void setHttpResponse(HttpResponse response, Class<T> clazz) {
		if(response != null) {
			StatusLine status = response.getStatusLine();

			if(status != null) {
				httpCode = status.getStatusCode();
			}

			if(httpCode > 0) {
				if("java.io.InputStream".equals(clazz.getCanonicalName())) {
					HttpEntity entity = response.getEntity();

					try {
						data = (T)entity.getContent();

					} catch (IllegalStateException e) {
						log.error(e.getMessage());

					} catch (IOException e) {
						log.error(e.getMessage());
					}

					return;
				}

				String responseText = getResponseText(response);

				T t = ClassUtils.createInstance(clazz);

				if(t instanceof String) {
					data = (T)responseText;

				} else if(t instanceof Boolean) {
					data = (T)new Boolean(StringUtils.toBool(responseText));

				} else if(t instanceof Integer) {
					data = (T)new Integer(StringUtils.toInt(responseText));

				} else if(t instanceof Long) {
					data = (T)new Long(StringUtils.toLong(responseText));

				} else if(t instanceof Float) {
					data = (T)new Float(StringUtils.toFloat(responseText));

				} else if(t instanceof Double) {
					data = (T)new Double(StringUtils.toDouble(responseText));

				} else if(httpCode == HttpStatus.SC_OK && StringUtils.isNotBlank(responseText)) {
					Gson g = new Gson();

					data = g.fromJson(responseText, clazz);
				}

				t = null;
			}
		}
	}

	/**
	 * 将HTTP返回解析成字符串
	 * @param response
	 * @return
	 */
	protected String getResponseText(HttpResponse response) {
		HttpEntity entity = response.getEntity();

		if(entity != null) {
			try {
				return EntityUtils.toString(entity, HTTP.UTF_8);

			} catch (ParseException e) {
				log.error("getResponseText() failed: ", e);

			} catch (IOException e) {
				log.error("getResponseText() failed: ", e);
			}
		}

		return null;
	}

}

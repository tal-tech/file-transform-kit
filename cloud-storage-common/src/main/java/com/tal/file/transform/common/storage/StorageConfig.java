package com.tal.file.transform.common.storage;


/**
 * 存储对象配置信息
 * 
 * @author lazycathome
 *
 */
public class StorageConfig {

	private String accessKey = "";
	private String secretKey = "";
	private String bucket = "";
	private String endpoint = "";
	private String prefixUrl = "";
	private String localPath = "";

	/**
	 * 通过内建 builder 创建配置对象
	 * @param builder
	 */
	protected StorageConfig(Builder builder) {
		accessKey = builder.accessKey;
		secretKey = builder.secretKey;
		bucket = builder.bucket;
		endpoint = builder.endpoint;
		prefixUrl = builder.prefixUrl;
		localPath = builder.localPath;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getPrefixUrl() {
		return prefixUrl;
	}

	public void setPrefixUrl(String prefixUrl) {
		this.prefixUrl = prefixUrl;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	/**
	 * 存储对象内置的配置信息构建类
	 * 
	 * @author lazycathome
	 *
	 */
	public static class Builder {
		private String accessKey = "";
		private String secretKey = "";
		private String bucket = "";
		private String endpoint = "";
		private String prefixUrl = "";
		private String localPath = "";

		public Builder setAccessKey(String accessKey) {
			this.accessKey = accessKey;
			
			return this;
		}

		public Builder setSecretKey(String secretKey) {
			this.secretKey = secretKey;

			return this;
		}

		public Builder setBucket(String bucket) {
			this.bucket = bucket;
			
			return this;
		}

		public Builder setEndpoint(String endpoint) {
			this.endpoint = endpoint;

			return this;
		}

		public Builder setPrefixUrl(String prefixUrl) {
			this.prefixUrl = prefixUrl;
			
			return this;
		}

		public Builder setLocalPath(String localPath) {
			this.localPath = localPath;

			return this;
		}

		public StorageConfig build() {
			return new StorageConfig(this);
		}

	}

}

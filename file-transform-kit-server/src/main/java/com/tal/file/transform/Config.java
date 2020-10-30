package com.tal.file.transform;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

	@Value("${storage.provider:''}")
	private String storageProvider = "";

	@Value("${storage.local.path:''}")
	private String storageLocalPath = "";
	@Value("${storage.endpoint:''}")
	private String storageEndpoint = "";
	@Value("${storage.prefix.url:''}")
	private String storagePrefixUrl = "";
	@Value("${storage.accessKey:''}")
	private String storageAccessKey = "";
	@Value("${storage.secretKey:''}")
	private String storageSecretKey = "";
	@Value("${storage.bucket:''}")
	private String storageBucket = "";
	@Value("${cmd.mct.duration:''}")
	private String cmdMctDuration = "";
	@Value("${cmd.mct.transfer:''}")
	private String cmdMctTransfer = "";
	@Value("${cmd.mct.thumbnail:''}")
	private String cmdMctThumbnail = "";

	public String getStorageProvider() {
		return storageProvider;
	}

	public void setStorageProvider(String storageProvider) {
		this.storageProvider = storageProvider;
	}

	public String getStorageLocalPath() {
		return storageLocalPath;
	}

	public void setStorageLocalPath(String storageLocalPath) {
		this.storageLocalPath = storageLocalPath;
	}

	public String getStorageEndpoint() {
		return storageEndpoint;
	}

	public void setStorageEndpoint(String storageEndpoint) {
		this.storageEndpoint = storageEndpoint;
	}

	public String getStoragePrefixUrl() {
		return storagePrefixUrl;
	}

	public void setStoragePrefixUrl(String storagePrefixUrl) {
		this.storagePrefixUrl = storagePrefixUrl;
	}

	public String getStorageAccessKey() {
		return storageAccessKey;
	}

	public void setStorageAccessKey(String storageAccessKey) {
		this.storageAccessKey = storageAccessKey;
	}

	public String getStorageSecretKey() {
		return storageSecretKey;
	}

	public void setStorageSecretKey(String storageSecretKey) {
		this.storageSecretKey = storageSecretKey;
	}

	public String getStorageBucket() {
		return storageBucket;
	}

	public void setStorageBucket(String storageBucket) {
		this.storageBucket = storageBucket;
	}

	public String getCmdMctDuration() {
		return cmdMctDuration;
	}

	public void setCmdMctDuration(String cmdMctDuration) {
		this.cmdMctDuration = cmdMctDuration;
	}

	public String getCmdMctTransfer() {
		return cmdMctTransfer;
	}

	public void setCmdMctTransfer(String cmdMctTransfer) {
		this.cmdMctTransfer = cmdMctTransfer;
	}

	public String getCmdMctThumbnail() {
		return cmdMctThumbnail;
	}

	public void setCmdMctThumbnail(String cmdMctThumbnail) {
		this.cmdMctThumbnail = cmdMctThumbnail;
	}

}

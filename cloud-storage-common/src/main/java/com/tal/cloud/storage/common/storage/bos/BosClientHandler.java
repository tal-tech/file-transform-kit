package com.tal.cloud.storage.common.storage.bos;

import com.baidubce.services.bos.BosClient;

/**
 * BOS客户端对象操作接口
 * 主要用于在运行时，对BOS客户端进行重置等操作
 * 防止发生意外时，可以通过重置客户端使客户端恢复正常
 * @author lazycathome
 *
 */
public interface BosClientHandler {

	public BosClient getClient();
	public void reset();

}

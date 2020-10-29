package com.tal.cloud.storage.node.media;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "media")
@Data
public class MediaTasks {

	private Map<String, List<MediaTask>> tasks = new HashMap<String, List<MediaTask>>();

	public List<MediaTask> find(String clientId) {
		if(tasks.containsKey(clientId)) {
			return tasks.get(clientId);
		}

		return null;
	}

}

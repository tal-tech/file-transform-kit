package com.tal.file.transform.image;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("image")
@Data
public class ImageTasks {

	private Map<String, List<ImageTask>> tasks = new HashMap<String, List<ImageTask>>();
	private Map<String, Boolean> paths = new HashMap<>();

	public List<ImageTask> find(String clientId) {
		if(tasks.containsKey(clientId)) {
			return tasks.get(clientId);
		}

		return null;
	}

	public boolean findPaths(String clientId) {
		if(paths.containsKey(clientId)) {
			return paths.get(clientId);
		}

		return true;
	}

}

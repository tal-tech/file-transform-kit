package com.tal.file.transform.controller.media.en;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties("media.presets")
public class MctPresets {

	private Map<String, String> presets = new HashMap<String, String>();

	public String find(String preset) {
		if(presets.containsKey(preset)) {
			return presets.get(preset);
		}

		return "";
	}

}

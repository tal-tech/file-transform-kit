package com.tal.file.transform.controller.media.en;

import com.tal.file.transform.Config;
import com.tal.file.transform.common.StringUtils;
import com.tal.file.transform.utils.ShellCommand;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MctPipeline {

	protected static final Logger log = Logger.getLogger(MctPipeline.class);

	@Autowired
    Config conf;

	@Autowired
	MctPresets presets;

	@Autowired
	MctQueue queue;
	
	public long getDuration(String filename) {
		ShellCommand cmd = new ShellCommand(conf.getCmdMctDuration()).setNamedArg("sourcePath", filename);

		cmd.run();

		List<String> result = cmd.getOutput();

		for(String r : result) {
			if(StringUtils.isNotBlank(r)) {
				Pattern pattern = Pattern.compile("Duration: (\\d+):(\\d+):(\\d+).(\\d+)");
		        Matcher m = pattern.matcher(r);

		        if(m.find()) {
		        	int seconds = StringUtils.toInt(m.group(3));
		        	int minutes = StringUtils.toInt(m.group(2));
		        	int hours = StringUtils.toInt(m.group(1));

		        	return seconds + minutes * 60 + hours * 3600;
				}
			}
		}

		return 0;
	}

	public void transfer(String srcPath, String targetPath, String preset, String mime) {
		ShellCommand cmd = new ShellCommand(conf.getCmdMctTransfer())
								.setNamedArg("sourcePath", srcPath)
								.setNamedArg("targetPath", targetPath)
								.setNamedArg("preset", presets.find(preset));

		queue.add(cmd);
	}

	public void thumbnail(String srcPath, String targetPath, String mime) {
		ShellCommand cmd = new ShellCommand(conf.getCmdMctThumbnail())
								.setNamedArg("sourcePath", srcPath)
								.setNamedArg("targetPath", targetPath);

		queue.add(cmd);
	}

}

package com.tal.cloud.storage.node.utils;

import com.tal.cloud.storage.common.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class CommandLineParser {

	public static List<String> parse(String cmdLine) {
		List<String> result = new ArrayList<String>();

		if(StringUtils.isNotBlank(cmdLine)) {
			String[] cmds = cmdLine.trim().split(" ");

			if(cmds != null) {
				int i = 0, l = cmds.length;

				while(i < l) {
					String cmd = cmds[i];

					if(StringUtils.isNotBlank(cmd)) {
						if(cmd.startsWith("\"")) {
							if(cmd.endsWith("\"")) {
								result.add(cmd.substring(1, cmd.length() - 1));

							} else {
								StringBuffer cls = new StringBuffer();

								cls.append(cmd.substring(1));

								for(i ++; i < l; i ++) {
									cmd = cmds[i];

									if(StringUtils.isNotBlank(cmd)) {
										if(cmd.endsWith("\"")) {
											cls.append(" ").append(cmd.substring(0, cmd.length() - 1));

											break;
										}

										cls.append(" ").append(cmd);

									} else {
										cls.append(" ");
									}
								}

								result.add(cls.toString());
							}

						} else {
							result.add(cmd);
						}
					}

					i ++;
				}
			}
		}

		return result;
	}

}

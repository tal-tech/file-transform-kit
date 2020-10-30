package com.tal.file.transform.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class CommandProcessor implements Runnable {

	private final Logger log = LoggerFactory.getLogger(CommandProcessor.class);

	@Override
	public void run() {
		Idler idler = new Idler();

		for(;;) {
			Command cmd = Commands.transfered.poll();

			if(cmd != null) {
				File local = cmd.getLocal();

				switch(cmd.getType()) {
				case Command.TYPE_IMAGE:
				case Command.TYPE_AUDIO:
				case Command.TYPE_VIDEO:
					break;

				case Command.TYPE_MISC:
					break;
				}

				local.delete();

			} else {
				idler.rest();
			}
		}
	}

}

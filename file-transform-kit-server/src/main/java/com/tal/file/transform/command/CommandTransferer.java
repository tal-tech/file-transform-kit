package com.tal.file.transform.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.tal.file.transform.common.FilenameUtils;
import com.tal.file.transform.common.mime.Mime;
import com.tal.file.transform.common.mime.MimeUtils;
import com.tal.file.transform.common.storage.StorageFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandTransferer implements Runnable {

	private final Logger log = LoggerFactory.getLogger(CommandTransferer.class);

	@Override
	public void run() {
		Idler idler = new Idler();

		for(;;) {
			Command cmd = Commands.untransfered.poll();

			if(cmd != null) {
				try {
					File local = cmd.getLocal();
					StorageFile file = cmd.getTarget();
					String ext = FilenameUtils.getExt(cmd.getFilename());
					Mime mime = MimeUtils.find(ext);

					file.write(new FileInputStream(local), mime);

					Commands.transfered.add(cmd);

				} catch (FileNotFoundException e) {
					log.error(e.getMessage());
				}

			} else {
				idler.rest();
			}
		}
	}

}

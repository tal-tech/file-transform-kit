package com.tal.file.transform.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程休眠器
 * 
 * @author liujt
 *
 */
public class Idler {

	static final Logger log = LoggerFactory.getLogger(Idler.class);

	private static final int TIME_SLEEP = 100;

	/**
	 * 休息一下
	 */
	public void rest() {
		rest(TIME_SLEEP);
	}

	public void rest(int idle) {
		try {
			Thread.sleep(idle);

		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}

}

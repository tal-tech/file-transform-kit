package com.tal.file.transform.controller.media.en;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Component
public class MctQueue {

	private ExecutorService executor = null;

	public void add(Runnable runnable) {
		if(executor == null) {
			executor = new ThreadPoolExecutor(1, 256, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		}

		executor.execute(runnable);
	}

}

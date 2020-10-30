package com.tal.file.transform.command;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Commands {

	public static Queue<Command> untransfered = new ConcurrentLinkedQueue<Command>();
	public static Queue<Command> transfered = new ConcurrentLinkedQueue<Command>();

}

package com.tal.cloud.storage.node.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellCommand implements Runnable {

	protected final Logger log = LoggerFactory.getLogger(ShellCommand.class);

	private List<String> cmds = new ArrayList<String>();
	private List<String> output = new ArrayList<String>();
	private int exitCode = 0;

	public ShellCommand() {

	}

	public ShellCommand(String cmdLine) {
		parseCmdLine(cmdLine);
	}

	public ShellCommand setCmdLine(String cmd) {
		parseCmdLine(cmd);

		return this;
	}

	public ShellCommand addArg(String cmd) {
		cmds.add(cmd);

		return this;
	}

	public ShellCommand addArgs(String args) {
		cmds.addAll(CommandLineParser.parse(args));

		return this;
	}

	public ShellCommand setNamedArg(String argName, String argVal) {
		String argKey = String.format("{%s}", argName.trim());

		for(int i = 0, c = cmds.size(); i < c; i ++) {
			if(cmds.get(i).equals(argKey)) {
				cmds.remove(i);
				cmds.addAll(i, CommandLineParser.parse(argVal));

				break;
			}
		}

		return this;
	}

	@Override
	public void run() {
		ProcessBuilder pb = new ProcessBuilder();

		pb.redirectErrorStream(true);

		pb.command(cmds);

		log.info("cmds:{}", cmds);

		try {
			Process proc = pb.start();

			readOutput(proc.getInputStream());

			try {
				proc.waitFor();

				exitCode = proc.exitValue();

			} catch (InterruptedException e) {
				log.error(e.getMessage());

				Thread.currentThread().interrupt();
			}

			proc.getErrorStream().close();

		} catch (IOException e) {
			log.error(e.getMessage());
		}

		exitCode = -1;
	}

	public List<String> getOutput() {
		return output;
	}

	public int getExitCode() {
		return exitCode;
	}

	protected void parseCmdLine(String cmd) {
		cmds = CommandLineParser.parse(cmd);
	}

	protected boolean readOutput(InputStream stream) {
		return readOutput(stream, Charset.forName("UTF-8"));
	}

	protected boolean readOutput(InputStream stream, Charset charset) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset.name()), 1024);
			String line = null;

			while ((line = reader.readLine()) != null) {
				output.add(line);
			}

			reader.close();

			return true;

		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());

		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return false;
	}

	public static void main(String[] args) {
		ShellCommand cmd = new ShellCommand("cmd /c dir {filePath}").setNamedArg("filePath", "C:\\Users\\Administrator\\Desktop");

		cmd.run();

		System.out.println(cmd.getExitCode());
		System.out.println(cmd.getOutput());
	}

}

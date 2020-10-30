package com.tal.file.transform.controller.result;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class ResultWriter implements Result {

	protected static final Logger log = Logger.getLogger(ResultWriter.class);

	private HttpServletResponse response = null;
	private Gson gson = null;

	public ResultWriter(HttpServletResponse response) {
		this.response = response;
		this.gson = new Gson();
	}

	@Override
	public void render(Object result) {
		render(gson.toJson(result));
	}

	@Override
	public void render(String result) {
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");

			Writer writer = response.getWriter();

			writer.write(result);

			writer.flush();
			writer.close();

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

}

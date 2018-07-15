package com.github.hackeklotz;

import com.intellij.openapi.diagnostic.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public class PylintExecutor {

	private static Logger logger = Logger.getInstance(PylintExecutor.class);

	public List<PylintError> execute(Path path, Path pylintPath, String commandLineParameters) {
		InputStream inputStream = executePylint(path, pylintPath, commandLineParameters);
		PylintJsonParser parser = new PylintJsonParser();
		return parser.parse(inputStream);
	}

	private InputStream executePylint(Path path, Path pylintPath, String commandLineParameters) {
		logger.debug("Run Pylint on " + path);

		ProcessBuilder processBuilder = new ProcessBuilder(pylintPath.toString(), "-f", "json",
				commandLineParameters, path.toString());
		try {
			logger.info("Running command " + processBuilder.command().toString());
			// Process process = processBuilder.start();
			String command = pylintPath + " -f json " + commandLineParameters + " " + path;
			logger.info("Running command " + command);
			Process process = Runtime.getRuntime().exec(command);
			return process.getInputStream();
		} catch (IOException e) {
			logger.error("Error while executing Pylint for file: " + path, e);
			return new ByteArrayInputStream(new byte[0]);
		}
	}


}

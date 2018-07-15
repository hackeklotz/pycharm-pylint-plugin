package com.github.hackeklotz;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

public class PylintExecutorTest {

	@Test
	public void parseLine() {
		URL url = getClass().getClassLoader().getResource("parser.py");		
		
		PylintExecutor executor = new PylintExecutor();
		List<PylintError> errors = executor.execute(Paths.get(url.getPath()), Paths.get("/usr/bin/pylint"), "");

		
		assertThat(errors).hasSize(21);
	}

	

}

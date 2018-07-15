package com.github.hackeklotz;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PylintJsonParserTest {
	
	
	@Test
	public void parse_emptyStream() {
		InputStream inputStream = new ByteArrayInputStream(new byte[0]);

		PylintJsonParser parser = new PylintJsonParser();
		List<PylintError> errors = parser.parse(inputStream);

		assertThat(errors).isEmpty();
	}

	@Test
	public void parse_errorCount() {

		InputStream inputStream = getClass().getResourceAsStream("/pylint.json");

		PylintJsonParser parser = new PylintJsonParser();
		List<PylintError> errors = parser.parse(inputStream);

		assertThat(errors).hasSize(4);
	}

	@Test
	public void parse_error() {

		InputStream inputStream = getClass().getResourceAsStream("/pylint.json");

		PylintJsonParser parser = new PylintJsonParser();
		List<PylintError> errors = parser.parse(inputStream);

		PylintError expectedError = new PylintError(PylintCategory.CONVENTION, 62, "some problem");
		assertThat(errors).contains(expectedError);
	}

}

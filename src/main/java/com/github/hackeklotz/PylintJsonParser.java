package com.github.hackeklotz;

import com.google.common.io.ByteStreams;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PylintJsonParser {

	public List<PylintError> parse(InputStream inputStream) {
		byte[] byteArray;
		try {
			byteArray = ByteStreams.toByteArray(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (byteArray.length == 0) {
			return new ArrayList<>();
		}

		Any errors = JsonIterator.deserialize(byteArray);
		List<PylintError> pylintErrors = new ArrayList<>();
		for (Any error : errors) {

			String type = error.get("type").toString();

			PylintCategory category = PylintCategory.getCategory(type);
			int line = error.get("line").toInt();
			String message = error.get("message").toString();

			PylintError pylintError = new PylintError(category, line, message);
			pylintErrors.add(pylintError);
		}

		return pylintErrors;
	}
}

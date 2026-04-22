package obt.index.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class TestOutputJSONSupport {
	private TestOutputJSONSupport() {}

	static Path createTempJsonFile(String prefix) throws IOException {
		return Files.createTempFile(prefix, ".json");
	}

	static String readFile(Path path) throws IOException {
		return Files.readString(path);
	}
}

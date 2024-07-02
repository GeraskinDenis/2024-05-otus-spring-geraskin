package ru.otus.hw.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StreamsIOServiceTest {

	private ByteArrayOutputStream outputStream;
	private PrintStream printStream;
	private StreamsIOService streamsIOService;

	@BeforeEach
	void beforeEach() {
		outputStream = new ByteArrayOutputStream();
		printStream = new PrintStream(outputStream);
		streamsIOService = new StreamsIOService(printStream);
	}

	@AfterEach
	void afterEach() {
		printStream.close();
	}

	@Test
	void printLine() {
		String testLine = "TEST LINE";
		streamsIOService.printLine(testLine);
		assertThat(outputStream.toString()).isEqualTo(testLine + System.lineSeparator());
	}

	@Test
	void printFormattedLine() {
		String testLine = String.format("%s %s %s", 1, 2, 3);
		streamsIOService.printFormattedLine("%s %s %s", 1, 2, 3);
		assertThat(outputStream.toString()).isEqualTo(testLine + System.lineSeparator());
	}
}
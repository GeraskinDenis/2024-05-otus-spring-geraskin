package ru.otus.hw.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StreamsIOServiceTest {

	private ByteArrayOutputStream outputStream;
	private PrintStream printStream;
	@Mock
	private InputStream inputStream;
	private StreamsIOService streamsIOService;

	@BeforeEach
	void beforeEach() {
		outputStream = new ByteArrayOutputStream();
		printStream = new PrintStream(outputStream);
		streamsIOService = new StreamsIOService(printStream, inputStream);
	}

	@AfterEach
	void afterEach() {
		printStream.close();
	}

	@Test
	void shouldPrintTheLineCorrectly() {
		String testLine = "TEST LINE";
		streamsIOService.printLine(testLine);
		assertThat(outputStream.toString()).isEqualTo(testLine + System.lineSeparator());
	}

	@Test
	void shouldPrintTheFormatedLineCorrectly() {
		String testLine = String.format("%s %s %s", 1, 2, 3);
		streamsIOService.printFormattedLine("%s %s %s", 1, 2, 3);
		assertThat(outputStream.toString()).isEqualTo(testLine + System.lineSeparator());
	}
}
package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StreamsIOServiceTest {
    @Mock
    private PrintStream printStream;
    @Mock
    private InputStream inputStream;
    @InjectMocks
    private StreamsIOService streamsIOService;

    @Test
    void shouldPrintTheLineCorrectly() {
        String testLine = "TEST LINE";
        streamsIOService.printLine(testLine);
        verify(printStream, times(1)).println(testLine);
    }

    @Test
    void shouldPrintTheFormattedLineCorrectly() {
        String fmt = "%s %s %s";
        streamsIOService.printFormattedLine(fmt, 1, 2, 3);
        verify(printStream, times(1)).printf(fmt + "%n", 1, 2, 3);
    }
}
package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.ReportConverter;
import ru.otus.hw.dto.Report;
import ru.otus.hw.services.GenreReportService;

@ShellComponent
@RequiredArgsConstructor
public class GenreReportCommands {
    private final GenreReportService reportService;

    private final ReportConverter converter;

    @ShellMethod(value = "Number of books by genre", key = "genre-report-1")
    public String numberOfBooksByGenre() {
        Report report = reportService.getNumberOfBooksByGenre();
        return converter.reportToString(report);
    }
}

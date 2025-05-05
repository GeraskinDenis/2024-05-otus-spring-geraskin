package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.ReportConverter;
import ru.otus.hw.services.GenreReportService;

import java.util.List;
import java.util.Objects;

@Command(command = "genre", group = "Genre commands")
@RequiredArgsConstructor
public class GenreReportCommands {

    private final GenreReportService reportService;

    private final ReportConverter reportConverter;

    @Command(command = "count-books", description = "count Books by Genres")
    public String countBooksByGenres(@Option(longNames = "ids", label = "Genre IDs", description = """
            Number of Books by all Genres, if parameter 'ids' is NULL or by Genre IDs. Example: '1,2,3'.
            """) List<Long> genreIds) {
        if (Objects.isNull(genreIds)) {
            return reportConverter.reportToString(reportService.countBooksByGenre());
        } else {
            return reportConverter.reportToString(reportService.countBooksByGenre(genreIds));
        }
    }
}

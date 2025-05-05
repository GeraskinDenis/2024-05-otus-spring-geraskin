package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.ReportConverter;
import ru.otus.hw.services.AuthorReportService;

import java.util.List;
import java.util.Objects;

@Command(command = "author", group = "Author commands")
@RequiredArgsConstructor
public class AuthorReportCommands {

    private final AuthorReportService authorReportService;

    private final ReportConverter reportConverter;

    @Command(command = "count-books", description = "count Books by Authors")
    public String countBooksByAuthors(@Option(longNames = "ids", label = "Author IDs", description = """
            Number of Books by all Authors, if parameter 'ids' is NULL or by Authors IDs. Example: '1,2,3'.
            """)
                                      List<Long> authorIds) {
        if (Objects.isNull(authorIds)) {
            return reportConverter.reportToString(authorReportService.countBooksByAuthors());
        } else {
            return reportConverter.reportToString(authorReportService.countBooksByAuthors(authorIds));
        }
    }
}


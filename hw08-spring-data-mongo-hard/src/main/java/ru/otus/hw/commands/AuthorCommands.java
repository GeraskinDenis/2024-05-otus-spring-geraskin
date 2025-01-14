package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.ReportConverter;
import ru.otus.hw.services.AuthorService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class AuthorCommands {

    private final AuthorService authorService;

    private final AuthorConverter authorConverter;

    private final ReportConverter reportConverter;

    @ShellMethod(value = "Delete author by ID", key = "ad")
    public String delete(@ShellOption(value = "id", help = "author ID to delete") String id) {
        authorService.deleteById(id);
        return "Deletion Author by ID (%s) completed.".formatted(id);
    }

    @ShellMethod(value = "Find all authors", key = "aa")
    public String findAll() {
        return authorService.findAll().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Find author by ID", key = "af")
    public String findById(@ShellOption(value = "id", help = "author ID for search") String id) {
        return authorService.findById(id)
                .map(authorConverter::authorToString)
                .orElse("Author not found by id: %s.".formatted(id));
    }

    @ShellMethod(value = "Get number of books by authors", key = "author-report-1")
    public String getNumberOfBooksByAuthors() {
        return reportConverter.reportToString(authorService.getNumberOfBooksByAuthors());
    }

    @ShellMethod(value = "Insert a new author", key = "ai")
    public String insert(@ShellOption(value = "full_name", help = "full name of the new author") String fullName) {
        return authorConverter.authorToString(authorService.insert(fullName));
    }

    @ShellMethod(value = "Update existing author", key = "au")
    public String update(@ShellOption(value = "id", help = "author ID to update") String id,
                         @ShellOption(value = "full_name", help = "new full name") String fullName) {
        return authorConverter.authorToString(authorService.update(id, fullName));
    }
}

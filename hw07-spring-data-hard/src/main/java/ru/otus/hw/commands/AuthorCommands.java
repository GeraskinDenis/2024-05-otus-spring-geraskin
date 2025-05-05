package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.services.AuthorService;

import java.util.stream.Collectors;

@Command(command = "author", group = "Author commands")
@RequiredArgsConstructor
public class AuthorCommands {

    private final AuthorService authorService;

    private final AuthorConverter authorConverter;

    @Command(command = "del", description = "Remove Author by ID")
    public String delete(@Option(longNames = "id", required = true, arityMin = 1, label = "Author ID") long id) {
        authorService.deleteById(id);
        return "Deletion Author by ID (%s) completed.".formatted(id);
    }

    @Command(command = "find-all", description = "find all Authors")
    public String findAll() {
        return authorService.findAll().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Command(command = "find", description = "find by ID")
    public String findById(@Option(longNames = "id", required = true, arityMin = 1, label = "Author ID") long id) {
        return authorService.findById(id)
                .map(authorConverter::authorToString)
                .orElse("Author not found by id (%d).".formatted(id));
    }

    @Command(command = "add", description = "add a new Author")
    public String insert(@Option(longNames = "name", required = true, label = "full name of the new Author")
                         String fullName) {
        return authorConverter.authorToString(authorService.save(0L, fullName));
    }

    @Command(command = "update", description = "update existing Author")
    public String update(@Option(longNames = "id", required = true, arityMin = 1, label = "Author ID to update")
                         long id,
                         @Option(longNames = "name", required = true, label = "new full name") String fullName) {
        return authorConverter.authorToString(authorService.save(id, fullName));
    }
}

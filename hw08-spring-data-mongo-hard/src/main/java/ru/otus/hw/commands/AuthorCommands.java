package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.services.AuthorService;

import java.util.Objects;
import java.util.stream.Collectors;

@Command(group = "Author commands", command = "author")
@RequiredArgsConstructor
public class AuthorCommands {

    private final AuthorService authorService;

    private final AuthorConverter authorConverter;

    @Command(command = "del", description = "delete author by ID")
    public String delete(@Option(longNames = "id", required = true, description = "author ID to delete") String id) {
        authorService.deleteById(id);
        return "Deletion Author by ID (%s) completed.".formatted(id);
    }

    @Command(command = "find", description = "find by ID (find all)")
    public String findById(@Option(
            longNames = "id",
            description = "author ID (empty for all)") String id) {
        if (Objects.isNull(id)) {
            return authorService.findAll().stream()
                    .map(authorService::toDto)
                    .map(authorConverter::authorToString)
                    .collect(Collectors.joining(System.lineSeparator()));
        } else {
            return authorService.findById(id).map(authorService::toDto)
                    .map(authorConverter::authorToString)
                    .orElse("Author not found by id: %s.".formatted(id));
        }
    }

    @Command(command = "find-by-name", description = "find by 'fullName' substring")
    public String findByFullName(@Option(
            longNames = "name",
            required = true,
            description = "'fullName' substring") String name) {
        return authorService.findByFullName(name).stream()
                .map(authorService::toDto)
                .map(authorConverter::authorToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Command(command = "add", description = "add a new author")
    public String insert(@Option(
            longNames = "name",
            required = true,
            description = "full name of the new author") String fullName) {
        return authorConverter.authorToString(authorService.toDto(authorService.insert(fullName)));
    }

    @Command(command = "update", description = "update existing author")
    public String update(
            @Option(longNames = "id", required = true, description = "author ID to update") String id,
            @Option(longNames = "name", required = true, description = "new full name") String fullName) {
        return authorConverter.authorToString(authorService.toDto(authorService.update(id, fullName)));
    }
}

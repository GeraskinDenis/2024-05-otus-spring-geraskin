package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.services.GenreService;

import java.util.Objects;
import java.util.stream.Collectors;

@Command(group = "Genre commands", command = "genre")
@RequiredArgsConstructor
public class GenreCommands {

    private final GenreService genreService;

    private final GenreConverter genreConverter;

    @Command(command = "del", description = "delete a genre by ID")
    public String delete(@Option(longNames = "id", required = true, description = "genre id to delete") String id) {
        genreService.deleteById(id);
        return "Deletion Genre by ID (%s) completed.".formatted(id);
    }

    @Command(command = "find", description = "find by ID (find all)")
    public String findById(@Option(longNames = "id", defaultValue = "", description = "genre ID (empty for all)") String id) {
        if (Objects.isNull(id)) {
            return genreService.findAll().stream()
                    .map(genreService::toDto)
                    .map(genreConverter::genreToString)
                    .collect(Collectors.joining(System.lineSeparator()));
        } else {
            return genreService.findById(id)
                    .map(genreService::toDto)
                    .map(genreConverter::genreToString)
                    .orElse("Genre not found by id (%s).".formatted(id));
        }
    }

    @Command(command = "add", description = "Add a new genre")
    public String insert(
            @Option(longNames = "name", required = true, description = "name of the new genre") String name) {
        return genreConverter.genreToString(genreService.toDto(genreService.insert(name)));
    }

    @Command(command = "update", description = "update a genre")
    public String update(
            @Option(longNames = "id", required = true, description = "genre id") String id,
            @Option(longNames = "name", required = true, description = "new genre name") String name) {
        return genreConverter.genreToString(genreService.toDto(genreService.update(id, name)));
    }
}

package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.services.GenreService;

import java.util.stream.Collectors;

@Command(command = "genre", group = "Genre commands")
@RequiredArgsConstructor
public class GenreCommands {

    private final GenreService genreService;

    private final GenreConverter genreConverter;

    @Command(command = "del", description = "delete the Genre by ID")
    public String delete(@Option(longNames = "id", required = true, arityMin = 1, label = "Genre ID to delete")
                         long id) {
        genreService.deleteById(id);
        return "Deletion Genre by ID (%s) completed.".formatted(id);
    }

    @Command(command = "find", description = "find Genre by ID")
    public String findById(@Option(longNames = "id", required = true, arityMin = 1, label = "Genre ID for search")
                           long id) {
        return genreService.findById(id)
                .map(genreConverter::genreToString)
                .orElse("Genre not found by id (%s).".formatted(id));
    }

    @Command(command = "find-all", description = "find all Genres")
    public String findAll() {
        return genreService.findAll().stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Command(command = "add", description = "add a new Genre")
    public String insert(@Option(longNames = "name", required = true, label = "name of the new Genre") String name) {
        return genreConverter.genreToString(genreService.save(0L, name));
    }

    @Command(command = "update", description = "update the Genre")
    public String update(@Option(longNames = "id", required = true, arityMin = 1, label = "ID of the Genre") long id,
                         @Option(longNames = "name", required = true, label = "name of the Genre") String name) {
        return genreConverter.genreToString(genreService.save(id, name));
    }
}

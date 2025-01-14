package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.services.GenreService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class GenreCommands {
    private final GenreService genreService;

    private final GenreConverter genreConverter;

    @ShellMethod(value = "Find genre by ID", key = "gf")
    public String findById(@ShellOption(value = "id", help = "genre ID for search") String id) {
        return genreService.findById(id)
                .map(genreConverter::genreToString)
                .orElse("Genre not found by id (%s).".formatted(id));
    }

    @ShellMethod(value = "Find all genres", key = "ga")
    public String findAll() {
        return genreService.findAll().stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Insert a new genre", key = "gi")
    public String insert(@ShellOption(value = "name", help = "name of the new genre") String name) {
        return genreConverter.genreToString(genreService.insert(name));
    }

    @ShellMethod(value = "Update a genre", key = "gu")
    public String update(@ShellOption(value = "id", help = "id of the genre") String id,
                         @ShellOption(value = "name", help = "name of the genre") String name) {
        return genreConverter.genreToString(genreService.update(id, name));
    }

    @ShellMethod(value = "Delete a genre by id", key = "gd")
    public String delete(@ShellOption(value = "id", help = "id of the genre to delete") String id) {
        genreService.deleteById(id);
        return "Deletion Genre by ID (%s) completed.".formatted(id);
    }
}

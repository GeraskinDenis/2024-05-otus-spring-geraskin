package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreService {

    void deleteById(String id);

    List<Genre> findAll();

    List<Genre> findAllByIdOrThrow(Set<String> ids);

    Optional<Genre> findById(String id);

    Genre findByIdOrThrow(String id);

    List<Genre> findByNameSubstring(String nameSubstring);

    Genre insert(String name);

    GenreDto toDto(Genre genre);

    Genre update(String id, String name);
}

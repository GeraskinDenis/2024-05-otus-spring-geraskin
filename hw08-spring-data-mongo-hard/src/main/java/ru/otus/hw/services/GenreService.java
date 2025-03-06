package ru.otus.hw.services;

import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreService {

    void deleteById(String id);

    Optional<Genre> findById(String id);

    Genre findByIdOrThrow(String id);

    List<Genre> findAllByIdOrThrow(Set<String> ids);

    List<Genre> findAll();

    Genre insert(String name);

    Genre update(String id, String name);
}

package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    Optional<GenreDto> findById(long id);

    List<GenreDto> findAll();

    GenreDto insert(String name);

    GenreDto update(long id, String name);

    void deleteById(long id);
}

package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    Optional<GenreDto> findById(String id);

    List<GenreDto> findAll();

    GenreDto insert(String name);

    GenreDto update(String id, String name);

    void deleteById(String id);
}

package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreService {

    Optional<GenreDto> findById(long id);

    List<Genre> findByIdInOrThrow(Set<Long> ids);

    List<GenreDto> findAll();

    void deleteById(long id);

    GenreDto save(long id, String name);

    GenreDto toDto(Genre genre);

    List<GenreDto> toDto(Set<Genre> genres);

    Genre toObject(GenreDto genreDto);

    List<Genre> toObject(Set<GenreDto> genreDtos);
}

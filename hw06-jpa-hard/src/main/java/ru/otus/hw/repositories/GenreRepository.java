package ru.otus.hw.repositories;

import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository {
    List<Genre> findAll();

    Optional<Genre> findById(Long id);

    List<Genre> findByIds(Set<Long> ids);

    Genre save(Genre genre);

    void deleteById(long id);
}

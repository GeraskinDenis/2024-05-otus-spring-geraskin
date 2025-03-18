package ru.otus.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Genre;

import java.util.List;

public interface GenreRepository extends ListCrudRepository<Genre, String> {
    List<Genre> findByNameLike(String nameSubstring);
}

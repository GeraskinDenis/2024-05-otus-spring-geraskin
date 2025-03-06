package ru.otus.hw.services;

import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    void deleteById(String id);

    List<Author> findAll();

    Optional<Author> findById(String id);

    Author findByIdOrThrow(String id);

    Author insert(String fullName);

    Author update(String id, String fullName);
}

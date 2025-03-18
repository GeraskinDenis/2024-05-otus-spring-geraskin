package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    void deleteById(String id);

    List<Author> findAll();

    Optional<Author> findById(String id);

    Author findByIdOrThrow(String id);

    List<Author> findByFullName(String fullNameSubstring);

    Author insert(String fullName);

    AuthorDto toDto(Author author);

    Author update(String id, String fullName);
}

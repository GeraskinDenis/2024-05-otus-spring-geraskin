package ru.otus.hw.services;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    void deleteById(String id);

    List<Book> findAll();

    List<Book> findByAuthorFullNameSubstring(String fullNameSubstring);

    Optional<Book> findById(String id);

    Book findByIdOrThrow(String id);

    List<Book> findByTitleSubstring(String titleSubstring);

    Book insert(String title, String authorId, Set<String> genreIds);

    Book update(String id, String title, String authorId, Set<String> genresIds);
}

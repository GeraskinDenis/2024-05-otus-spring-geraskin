package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {

    void deleteById(String id);

    List<BookDto> findAll();

    List<BookDto> findByAuthorFullNameLike(String authorFullNameSubstring);

    Optional<BookDto> findById(String id);

    List<BookDto> findByTitleLike(String title);

    Optional<BookDto> findWithMaxId();

    BookDto insert(String title, String authorId, Set<Long> genreIds);

    BookDto update(String id, String title, String authorId, Set<Long> genresIds);
}

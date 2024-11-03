package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookDto> findById(long id);

    List<BookDto> findAll();

    List<BookDto> findByTitleLike(String title);

    List<BookDto> findByAuthorFullNameLike(String authorFullNameSubstring);

    Optional<BookDto> findWithMaxId();

    BookDto insert(String title, long authorId, Set<Long> genreIds);

    BookDto update(long id, String title, long authorId, Set<Long> genresIds);

    void deleteById(long id);
}

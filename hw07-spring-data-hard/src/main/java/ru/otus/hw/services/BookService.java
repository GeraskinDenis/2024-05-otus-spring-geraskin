package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {

    void deleteById(long id);

    List<BookDto> findAll();

    List<BookDto> findByAuthorFullNameLike(String authorFullNameSubstring);

    Optional<BookDto> findById(long id);

    Book findByIdOrThrow(long id);

    List<BookDto> findByTitleLike(String titleSubstring);

    Optional<BookDto> findWithMaxId();

    BookDto save(long id, String title, long authorId, Set<Long> genreIds);

    BookDto toDto(Book book);

    Book toObject(BookDto bookDto);
}

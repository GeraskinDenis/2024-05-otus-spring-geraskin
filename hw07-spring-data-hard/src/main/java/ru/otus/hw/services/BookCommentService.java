package ru.otus.hw.services;

import ru.otus.hw.dto.BookCommentDto;

import java.util.List;
import java.util.Optional;

public interface BookCommentService {

    void deleteAllByBookId(long bookId);

    void deleteById(long bookCommentId);

    List<BookCommentDto> findByBook(long id);

    Optional<BookCommentDto> findById(long id);

    Integer getNumberByBookId(Long id);

    BookCommentDto insert(long bookId, String text);

    BookCommentDto update(long id, long bookId, String text);
}

package ru.otus.hw.services;

import ru.otus.hw.dto.BookCommentDto;

import java.util.List;
import java.util.Optional;

public interface BookCommentService {

    void deleteAllByBookId(String bookId);

    void deleteById(String bookCommentId);

    List<BookCommentDto> findByBook(String id);

    Optional<BookCommentDto> findById(String id);

    Integer getNumberByBookId(String id);

    BookCommentDto insert(String bookId, String text);

    BookCommentDto update(String id, String bookId, String text);
}

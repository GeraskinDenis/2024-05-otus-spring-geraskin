package ru.otus.hw.services;

import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookCommentService {

    Integer countByBookId(String bookId);

    void deleteAllByBook(String bookId);

    void deleteById(String id);

    List<BookComment> findByBookId(String bookId);

    Optional<BookComment> findById(String id);

    BookComment findByIdOrThrow(String id);

    BookComment insert(String bookId, String text);

    BookComment update(String id, String bookId, String text);
}

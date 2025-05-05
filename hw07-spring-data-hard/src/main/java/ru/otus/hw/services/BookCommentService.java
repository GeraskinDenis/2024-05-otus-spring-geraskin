package ru.otus.hw.services;

import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookCommentService {

    Integer countByBookId(Long id);

    void deleteAllByBookId(long bookId);

    void deleteById(long bookCommentId);

    List<BookCommentDto> findByBookId(long id);

    Optional<BookCommentDto> findById(long id);

    BookComment findByIdOrThrow(long id);

    BookCommentDto save(long id, long bookId, String text);
}

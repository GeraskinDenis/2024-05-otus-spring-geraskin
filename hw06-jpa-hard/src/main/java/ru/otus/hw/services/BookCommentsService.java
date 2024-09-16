package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCommentDto;

import java.util.List;
import java.util.Optional;

public interface BookCommentsService {
    Optional<BookCommentDto> findById(long id);

    @Transactional(readOnly = true)
    List<BookCommentDto> findAllByBookId(long id);

    @Transactional
    BookCommentDto insert(long bookId, String text);

    @Transactional
    BookCommentDto update(long id, long bookId, String text);

    @Transactional
    void deleteById(long bookCommentId);

    @Transactional
    void deleteAllByBookId(long bookId);
}

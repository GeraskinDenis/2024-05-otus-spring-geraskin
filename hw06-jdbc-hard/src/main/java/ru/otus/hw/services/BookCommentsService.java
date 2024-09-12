package ru.otus.hw.services;

import ru.otus.hw.dto.BookCommentDto;

import java.util.Optional;

public interface BookCommentsService {
    Optional<BookCommentDto> findById(long id);
}

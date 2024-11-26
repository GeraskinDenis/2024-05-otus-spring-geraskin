package ru.otus.hw.mappers;

import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.models.BookComment;

public interface BookCommentMapper {
    BookCommentDto toDto(BookComment bookComment);
}

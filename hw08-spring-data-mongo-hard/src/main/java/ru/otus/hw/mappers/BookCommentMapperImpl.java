package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.models.BookComment;

@Component
public class BookCommentMapperImpl implements BookCommentMapper {

    @Override
    public BookCommentDto toDto(BookComment bookComment) {
        return new BookCommentDto(bookComment.getId(),
                bookComment.getUuid(),
                bookComment.getBook().getId(),
                bookComment.getText());
    }
}

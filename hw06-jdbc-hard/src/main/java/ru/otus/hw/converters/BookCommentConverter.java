package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookCommentDto;

@Component
public class BookCommentConverter {
    public String bookCommentToString(BookCommentDto bookCommentDto) {
        return "Id: %s, BookId: %s, Text: %s"
                .formatted(bookCommentDto.id(), bookCommentDto.bookId(), bookCommentDto.text());
    }
}

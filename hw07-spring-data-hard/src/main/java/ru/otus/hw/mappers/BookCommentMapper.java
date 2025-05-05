package ru.otus.hw.mappers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.services.BookService;

@Component
public class BookCommentMapper implements Mapper<BookComment, BookCommentDto> {

    private final BookService bookService;

    private final Mapper<Book, BookDto> bookMapper;

    public BookCommentMapper(BookService bookService, @Qualifier("bookMapper") Mapper<Book, BookDto> bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookCommentDto toDto(BookComment bookComment) {
        return new BookCommentDto(bookComment.getId(), bookMapper.toDto(bookComment.getBook()), bookComment.getText());
    }

    @Override
    public BookComment toObject(BookCommentDto dto) {
        return new BookComment(dto.id(), bookMapper.toObject(dto.book()), dto.text());
    }
}

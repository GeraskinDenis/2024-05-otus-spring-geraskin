package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookDto toDto(Book book) {
        return new BookDto(book.getId(), book.getTitle(), book.getAuthor(), book.getGenres().stream().toList());
    }
}

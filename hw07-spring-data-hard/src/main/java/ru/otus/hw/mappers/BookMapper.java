package ru.otus.hw.mappers;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

public interface BookMapper {
    BookDto toDto(Book book);
}

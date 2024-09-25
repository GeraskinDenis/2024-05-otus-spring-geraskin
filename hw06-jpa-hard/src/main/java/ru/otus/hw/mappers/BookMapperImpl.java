package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

@Component
@RequiredArgsConstructor
public class BookMapperImpl implements BookMapper {

    private final AuthorMapper authorMapper;

    private final GenreMapper genreMapper;

    private final BookCommentMapper bookCommentMapper;

    @Override
    public BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                authorMapper.toDto(book.getAuthor()),
                book.getGenres().stream().map(genreMapper::toDto).toList(),
                book.getComments().stream().map(bookCommentMapper::toDto).toList());
    }
}

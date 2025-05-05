package ru.otus.hw.mappers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

@Component
public class BookMapper implements Mapper<Book, BookDto> {

    private final Mapper<Author, AuthorDto> authorMapper;

    private final Mapper<Genre, GenreDto> genreMapper;

    public BookMapper(@Qualifier("authorMapper") Mapper<Author, AuthorDto> authorMapper,
                      @Qualifier("genreMapper") Mapper<Genre, GenreDto> genreMapper) {
        this.authorMapper = authorMapper;
        this.genreMapper = genreMapper;
    }

    @Override
    public BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                authorMapper.toDto(book.getAuthor()),
                book.getGenres().stream().map(genreMapper::toDto).toList());
    }

    @Override
    public Book toObject(BookDto object) {
        return null;
    }
}

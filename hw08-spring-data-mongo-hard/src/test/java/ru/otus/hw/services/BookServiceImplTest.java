package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mappers.*;
import ru.otus.hw.repositories.*;

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис работы с Книгами")
@DataJpaTest
@Import({BookServiceImpl.class, BookMapperImpl.class, AuthorMapperImpl.class,
        GenreMapperImpl.class, BookCommentMapperImpl.class})
public class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @DisplayName("Должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getBookDtos")
    void shouldReturnCorrectBookById(BookDto expectedBookDto) {
        assertThat(bookService.findById(expectedBookDto.id()))
                .isPresent().contains(expectedBookDto);
    }

    @DisplayName("Должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookService.findAll();
        var expectedBooks = getBookDtos();
        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var authorDto = new AuthorDto(1L, "Author_1");
        var genreDtos = List.of(new GenreDto(1, "Genre_1"), new GenreDto(2, "Genre_2"));
        var expectedBookDto = new BookDto(4, "BookTitle_10500", authorDto, genreDtos);
        var returnedBookDto = bookService.insert("BookTitle_10500", 1L, Set.of(1L, 2L));
        assertThat(returnedBookDto).isNotNull()
                .matches(bookDto -> bookDto.id() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBookDto);
        assertThat(bookService.findById(returnedBookDto.id())).contains(expectedBookDto);
    }

    @DisplayName("Должен сохранять измененную книгу")
    @Test
    void shouldUpdatedBook() {
        var authorDto = new AuthorDto(1L, "Author_1");
        var genreDtos = List.of(new GenreDto(1L, "Genre_1"), new GenreDto(2L, "Genre_2"));
        var expectedBookDto = new BookDto(1L, "BookTitle_10500", authorDto, genreDtos);
        var returnedBookDto = bookService.update(1L, "BookTitle_10500", 1L, Set.of(1L, 2L));
        assertThat(returnedBookDto).isEqualTo(expectedBookDto);
        assertThat(bookService.findById(returnedBookDto.id())).contains(expectedBookDto);
    }

    @DisplayName("Должен удалять книгу по id ")
    @ParameterizedTest
    @MethodSource("getBookDtos")
    void shouldDeleteBook(BookDto bookDto) {
        assertThat(bookService.findById(bookDto.id()))
                .isNotNull()
                .hasValue(bookDto);
        bookService.deleteById(bookDto.id());
        assertThat(bookService.findById(bookDto.id())).isEmpty();
    }

    private static List<AuthorDto> getAuthorDtos() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .toList();
    }

    private static List<GenreDto> getGenreDtos() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreDto(id, "Genre_" + id))
                .toList();
    }

    private static Map<Long, List<BookCommentDto>> getBookCommentDtos() {
        Map<Long, List<BookCommentDto>> booksCommentDtos = new HashMap<>();
        long idBookComment = 1;
        for (Long idBook = 1L; idBook < 4L; idBook++) {
            List<BookCommentDto> bookCommentDtos = new ArrayList<>(3);
            for (int i = 1; i < 4; i++) {
                bookCommentDtos.add(new BookCommentDto(idBookComment++, idBook, "Book_%s_comment_%s".formatted(idBook, i)));
            }
            booksCommentDtos.put(idBook, bookCommentDtos);
        }
        return booksCommentDtos;
    }

    private static List<BookDto> getBookDtos(List<AuthorDto> authorDtos, List<GenreDto> genreDtos, Map<Long, List<BookCommentDto>> booksCommentDtos) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookDto(id,
                        "BookTitle_" + id,
                        authorDtos.get(id - 1),
                        genreDtos.subList((id - 1) * 2, (id - 1) * 2 + 2)))
                .toList();
    }

    private static List<BookDto> getBookDtos() {
        var authorDtos = getAuthorDtos();
        var genreDtos = getGenreDtos();
        var bookCommentDtos = getBookCommentDtos();
        return getBookDtos(authorDtos, genreDtos, bookCommentDtos);
    }


}

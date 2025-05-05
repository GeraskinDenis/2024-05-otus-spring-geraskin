package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

// В этом тесте не используется аннотация `@DataJpaTest`,
// потому что данная аннотация отключает Liquibase
@DisplayName("Репозиторий на основе JPA для работы с 'Book'")
@SpringBootTest // поднимает Контекст приложения
// по умолчанию загружается основная конфигурация `src/main/resource/application.yml`
// следующая аннотация заменяет значения указанных параметров для выполнения тестов
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb", "spring.shell.interactive.enabled=false"})
@AutoConfigureTestEntityManager // добавляем `org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager`,
// который не доступен с аннотацией `@SpringBootTest`, а доступен с аннотацией `@DataJpaTest`
@Transactional // Откатывает изменения после теста
public class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @DisplayName("Should find a Book by ID")
    @ParameterizedTest
    @MethodSource("getBookDtos")
    void shouldReturnCorrectBookById(BookDto expectedBookDto) {
        assertThat(bookService.findById(expectedBookDto.id()))
                .isPresent().contains(expectedBookDto);
    }

    @DisplayName("Should find all Books")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookService.findAll();
        var expectedBooks = getBookDtos();
        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("Should save a new Book")
    @Test
    void shouldSaveNewBook() {
        var authorDtos = getAuthorDtos();
        var genreDtos = getGenreDtos();
        var expectedBookDto = new BookDto(0L, "BookTitle_10500", authorDtos.get(1L),
                List.of(genreDtos.get(1L), genreDtos.get(2L)));
        var returnedBookDto = bookService.save(expectedBookDto);
        expectedBookDto = new BookDto(returnedBookDto.id(), "BookTitle_10500", authorDtos.get(1L),
                List.of(genreDtos.get(1L), genreDtos.get(2L)));
        assertThat(returnedBookDto).isEqualTo(expectedBookDto);
        assertThat(bookService.findById(returnedBookDto.id())).contains(expectedBookDto);
    }

    @DisplayName("Should update a Book")
    @Test
    void shouldUpdateBook() {
        var authorDtos = getAuthorDtos();
        var genreDtos = getGenreDtos();
        var expectedBookDto = new BookDto(1L, "BookTitle_10500", authorDtos.get(2L),
                List.of(genreDtos.get(3L), genreDtos.get(4L)));
        var returnedBookDto = bookService.save(expectedBookDto);
        assertThat(returnedBookDto).isEqualTo(expectedBookDto);
        assertThat(bookService.findById(returnedBookDto.id())).contains(returnedBookDto);
    }

    @DisplayName("Should remove a Book by ID")
    @ParameterizedTest
    @MethodSource("getBookDtos")
    void shouldDeleteBook(BookDto bookDto) {
        assertThat(bookService.findById(bookDto.id()))
                .isNotNull()
                .hasValue(bookDto);
        bookService.deleteById(bookDto.id());
        assertThat(bookService.findById(bookDto.id())).isEmpty();
    }

    private static Map<Long, AuthorDto> getAuthorDtos() {
        return IntStream.range(1, 5).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .collect(Collectors.toMap(AuthorDto::id, o -> o));
    }

    private static List<BookDto> getBookDtos() {
        var authorDtos = getAuthorDtos();
        var genreDtos = getGenreDtos();
        var bookDtos = new ArrayList<BookDto>();
        bookDtos.add(new BookDto(1L, "BookTitle_1", authorDtos.get(1L),
                List.of(genreDtos.get(1L), genreDtos.get(2L))));
        bookDtos.add(new BookDto(2L, "BookTitle_2", authorDtos.get(2L),
                List.of(genreDtos.get(3L), genreDtos.get(4L))));
        bookDtos.add(new BookDto(3L, "BookTitle_3", authorDtos.get(3L),
                List.of(genreDtos.get(5L), genreDtos.get(6L))));
        bookDtos.add(new BookDto(4L, "BookTitle_4", authorDtos.get(3L),
                List.of(genreDtos.get(1L), genreDtos.get(6L))));
        bookDtos.add(new BookDto(5L, "BookTitle_5", authorDtos.get(3L),
                List.of(genreDtos.get(2L), genreDtos.get(4L))));
        return bookDtos;
    }

    private static Map<Long, GenreDto> getGenreDtos() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreDto(id, "Genre_" + id))
                .collect(Collectors.toMap(GenreDto::id, o -> o));
    }
}

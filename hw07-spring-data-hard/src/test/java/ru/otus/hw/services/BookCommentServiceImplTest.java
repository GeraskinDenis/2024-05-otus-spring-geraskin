package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

// В этом тесте не используется аннотация `@DataJpaTest`,
// потому что данная аннотация отключает Liquibase
@DisplayName("Репозиторий на основе JPA для работы с 'Авторами'")
@SpringBootTest // поднимает Контекст приложения
// по умолчанию загружается основная конфигурация `src/main/resource/application.yml`
// следующая аннотация заменяет значения указанных параметров для выполнения тестов
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb", "spring.shell.interactive.enabled=false"})
@AutoConfigureTestEntityManager // добавляем `org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager`,
// который не доступен с аннотацией `@SpringBootTest`, а доступен с аннотацией `@DataJpaTest`
@Transactional // Откатывает изменения после теста
public class BookCommentServiceImplTest {

    @Autowired
    private BookCommentServiceImpl bookCommentService;

    private static List<BookCommentDto> bookCommentDtos;

    @BeforeEach
    public void beforeEach() {
        bookCommentDtos = getBookCommentDtos();
    }

    @DisplayName("Should find a Book comment by ID")
    @ParameterizedTest
    @MethodSource("getBookCommentDtos")
    void shouldReturnCorrectById(BookCommentDto expected) {
        assertThat(bookCommentService.findById(expected.id())).contains(expected);
    }

    @DisplayName("Should find all book comments by Book iD")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void shouldReturnCorrectBookCommentsByBookId(Long bookId) {
        List<BookCommentDto> expectedBookCommentsList = bookCommentDtos.stream()
                .filter(o -> o.book().id() == bookId).toList();
        List<BookCommentDto> actualBookCommentsList = bookCommentService.findByBookId(bookId);
        assertThat(actualBookCommentsList).isNotNull()
                .containsExactlyElementsOf(expectedBookCommentsList);
    }

    @DisplayName("Should save a new Book comments")
    @ParameterizedTest
    @MethodSource("getNewBookCommentDtos")
    void shouldInsertNewBookComment(BookCommentDto expectedBookCommentDto) {
        assertThat(bookCommentService.findById(expectedBookCommentDto.id()))
                .isEmpty();
        BookCommentDto returnedBookCommentDto = bookCommentService.save(expectedBookCommentDto);
        expectedBookCommentDto = new BookCommentDto(returnedBookCommentDto.id(), expectedBookCommentDto.book(),
                expectedBookCommentDto.text());
        assertThat(returnedBookCommentDto).isEqualTo(expectedBookCommentDto);
        assertThat(bookCommentService.findById(returnedBookCommentDto.id()))
                .isPresent().contains(expectedBookCommentDto);
    }

    @DisplayName("Should save changed Book comments correctly")
    @ParameterizedTest
    @MethodSource("getChangedBookCommentDtos")
    void shouldUpdateBookComment(BookCommentDto expectedBookCommentDto) {
        assertThat(bookCommentService.findById(expectedBookCommentDto.id()))
                .isNotEqualTo(Optional.of(expectedBookCommentDto));
        BookCommentDto returnedBookComment = bookCommentService.save(expectedBookCommentDto.id(),
                expectedBookCommentDto.book().id(), expectedBookCommentDto.text());
        assertThat(returnedBookComment).isEqualTo(expectedBookCommentDto);
        assertThat(bookCommentService.findById(expectedBookCommentDto.id()))
                .isEqualTo(Optional.of(expectedBookCommentDto));
    }

    @DisplayName("Should remove Book comment by ID")
    @ParameterizedTest
    @MethodSource("getBookCommentDtos")
    void shouldDeleteBookCommentById(BookCommentDto bookCommentDto) {
        assertThat(bookCommentService.findById(bookCommentDto.id())).contains(bookCommentDto);
        bookCommentService.deleteById(bookCommentDto.id());
        assertThat(bookCommentService.findById(bookCommentDto.id())).isEmpty();
    }

    @DisplayName("Should remove all Book comments by Book ID")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void shouldDeleteBookCommentById(Long bookId) {
        List<BookCommentDto> expectedBookCommentsList = bookCommentDtos.stream()
                .filter(o -> o.book().id() == bookId).toList();
        assertThat(bookCommentService.findByBookId(bookId))
                .containsExactlyElementsOf(expectedBookCommentsList);
        bookCommentService.deleteAllByBookId(bookId);
        assertThat(bookCommentService.findByBookId(bookId))
                .isEmpty();
    }

    private static Map<Long, AuthorDto> getAuthorDtos() {
        return IntStream.range(1, 5).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .collect(Collectors.toMap(AuthorDto::id, o -> o));
    }

    private static List<BookCommentDto> getBookCommentDtos() {
        var bookDtos = getBookDtos();
        var bookCommentDtos = new ArrayList<BookCommentDto>();
        long id = 1;
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(1L), "Book_1_comment_1"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(1L), "Book_1_comment_2"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(1L), "Book_1_comment_3"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(2L), "Book_2_comment_1"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(2L), "Book_2_comment_2"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(2L), "Book_2_comment_3"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(3L), "Book_3_comment_1"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(3L), "Book_3_comment_2"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(3L), "Book_3_comment_3"));
        return bookCommentDtos;
    }

    private static Map<Long, BookDto> getBookDtos() {
        var authorDtos = getAuthorDtos();
        var genreDtos = getGenreDtos();
        var bookDtos = new HashMap<Long, BookDto>();
        bookDtos.put(1L, new BookDto(1L, "BookTitle_1", authorDtos.get(1L),
                List.of(genreDtos.get(1L), genreDtos.get(2L))));
        bookDtos.put(2L, new BookDto(2L, "BookTitle_2", authorDtos.get(2L),
                List.of(genreDtos.get(3L), genreDtos.get(4L))));
        bookDtos.put(3L, new BookDto(3L, "BookTitle_3", authorDtos.get(3L),
                List.of(genreDtos.get(5L), genreDtos.get(6L))));
        bookDtos.put(4L, new BookDto(4L, "BookTitle_4", authorDtos.get(3L),
                List.of(genreDtos.get(1L), genreDtos.get(6L))));
        bookDtos.put(5L, new BookDto(5L, "BookTitle_5", authorDtos.get(3L),
                List.of(genreDtos.get(2L), genreDtos.get(4L))));
        return bookDtos;
    }

    private static List<Long> getBookIds() {
        return LongStream.range(1, 6).boxed().toList();
    }

    private static List<BookCommentDto> getChangedBookCommentDtos() {
        var bookDtos = getBookDtos();
        var bookCommentDtos = new ArrayList<BookCommentDto>();
        long id = 1;
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(1L), "Changed_Book_1_comment_1"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(1L), "Changed_Book_1_comment_2"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(1L), "Changed_Book_1_comment_3"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(2L), "Changed_Book_2_comment_1"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(2L), "Changed_Book_2_comment_2"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(2L), "Changed_Book_2_comment_3"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(3L), "Changed_Book_3_comment_1"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(3L), "Changed_Book_3_comment_2"));
        bookCommentDtos.add(new BookCommentDto(id++, bookDtos.get(3L), "Changed_Book_3_comment_3"));
        return bookCommentDtos;
    }

    private static Map<Long, GenreDto> getGenreDtos() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreDto(id, "Genre_" + id))
                .collect(Collectors.toMap(GenreDto::id, o -> o));
    }

    private static List<BookCommentDto> getNewBookCommentDtos() {
        var bookDtos = getBookDtos();
        var bookCommentDtos = new ArrayList<BookCommentDto>();
        bookCommentDtos.add(new BookCommentDto(0, bookDtos.get(1L), "New_Book_1_comment"));
        bookCommentDtos.add(new BookCommentDto(0, bookDtos.get(2L), "New_Book_2_comment"));
        bookCommentDtos.add(new BookCommentDto(0, bookDtos.get(3L), "New_Book_3_comment"));
        return bookCommentDtos;
    }
}

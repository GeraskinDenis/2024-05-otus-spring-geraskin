package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.mappers.BookCommentMapperImpl;
import ru.otus.hw.repositories.BookCommentsRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис работы с Комментариями книг")
@DataJpaTest
@Import({BookCommentServiceImpl.class, BookCommentsRepository.class,
        BookCommentMapperImpl.class, BookRepository.class})
public class BookCommentServiceImplTest {

    @Autowired
    private BookCommentServiceImpl bookCommentService;

    private static List<BookCommentDto> bookCommentDtos;

    @BeforeEach
    public void beforeEach() {
        bookCommentDtos = getBookCommentDtos();
    }

    @DisplayName("Должен загружать Комментарий книги по ID")
    @ParameterizedTest
    @MethodSource("getBookCommentDtos")
    void shouldReturnCorrectById(BookCommentDto bookCommentDto) {
        assertThat(bookCommentService.findById(bookCommentDto.id()))
                .isPresent().contains(bookCommentDto);
    }

    @DisplayName("Должен загрузить список Комментариев по ID книг")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void shouldReturnCorrectBookCommentsByBookId(Long bookId) {
        List<BookCommentDto> expectedBookCommentsList = getBookCommentDtosByBookId(bookId);
        List<BookCommentDto> actualBookCommentsList = bookCommentService.findByBook(bookId);
        assertThat(actualBookCommentsList).isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(expectedBookCommentsList);
    }

    @DisplayName("Должен корректно сохранить новые Комментарии к книгам")
    @ParameterizedTest
    @MethodSource("getNewBookCommentDtos")
    void shouldInsertNewBookComment(BookCommentDto expectedBookCommentDto) {
        assertThat(bookCommentService.findById(expectedBookCommentDto.id()))
                .isEmpty();
        BookCommentDto returnedBookCommentDto = bookCommentService
                .insert(expectedBookCommentDto.bookId(), expectedBookCommentDto.text());
        assertThat(returnedBookCommentDto).isNotNull()
                .matches(dto -> dto.id() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBookCommentDto);
        assertThat(bookCommentService.findById(returnedBookCommentDto.id()))
                .isPresent().contains(expectedBookCommentDto);
    }

    @DisplayName("Должен корректно сохранить измененные Комментарии")
    @ParameterizedTest
    @MethodSource("getChangedBookCommentDtos")
    void shouldUpdateBookComment(BookCommentDto expectedBookCommentDto) {
        assertThat(bookCommentService.findById(expectedBookCommentDto.id()))
                .isNotEqualTo(Optional.of(expectedBookCommentDto));
        BookCommentDto returnedBookComment = bookCommentService.update(expectedBookCommentDto.id(),
                expectedBookCommentDto.bookId(), expectedBookCommentDto.text());
        assertThat(returnedBookComment).isEqualTo(expectedBookCommentDto);
        assertThat(bookCommentService.findById(expectedBookCommentDto.id()))
                .isEqualTo(Optional.of(expectedBookCommentDto));
    }

    @DisplayName("Должен удалить Комментарий по ID")
    @ParameterizedTest
    @MethodSource("getBookCommentDtos")
    void shouldDeleteBookCommentById(BookCommentDto bookCommentDto) {
        assertThat(bookCommentService.findById(bookCommentDto.id())).isEqualTo(Optional.of(bookCommentDto));
        bookCommentService.deleteById(bookCommentDto.id());
        assertThat(bookCommentService.findById(bookCommentDto.id())).isNotEqualTo(Optional.of(bookCommentDto));
    }

    @DisplayName("Должен удалить все Комментарий по ID книги")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void shouldDeleteBookCommentById(Long bookId) {
        assertThat(bookCommentService.findByBook(bookId))
                .containsExactlyElementsOf(getBookCommentDtosByBookId(bookId));
        bookCommentService.deleteAllByBookId(bookId);
        assertThat(bookCommentService.findByBook(bookId))
                .isEmpty();
    }

    private static List<Long> getBookIds() {
        return LongStream.range(1, 4).boxed().toList();
    }

    private static List<BookCommentDto> getBookCommentDtos() {
        List<BookCommentDto> bookCommentDtos = new ArrayList<>();
        long idBookComment = 1;
        for (Long idBook = 1L; idBook < 4L; idBook++) {
            for (int i = 1; i < 4; i++) {
                bookCommentDtos.add(new BookCommentDto(idBookComment++, idBook, "Book_%s_comment_%s".formatted(idBook, i)));
            }
        }
        return bookCommentDtos;
    }

    private static List<BookCommentDto> getChangedBookCommentDtos() {
        List<BookCommentDto> bookCommentDtos = new ArrayList<>();
        long idBookComment = 1;
        for (Long idBook = 3L; idBook > 0; idBook--) {
            for (int i = 3; i > 0; i--) {
                bookCommentDtos.add(new BookCommentDto(idBookComment++, idBook, "Changed_Book_%s_comment_%s".formatted(idBook, i)));
            }
        }
        return bookCommentDtos;
    }

    private static List<BookCommentDto> getNewBookCommentDtos() {
        AtomicLong bookCommentId = new AtomicLong(9L);
        return getBookIds().stream().map(bookId ->
                        new BookCommentDto(bookCommentId.incrementAndGet(), bookId, "New_text_comment_" + bookId))
                .toList();
    }

    private static List<BookCommentDto> getBookCommentDtosByBookId(Long bookId) {
        return bookCommentDtos.stream().filter(e -> e.bookId() == bookId).toList();

    }
}

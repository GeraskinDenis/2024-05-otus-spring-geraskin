package ru.otus.hw.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Test set for 'BookCommentServiceImpl'")
@SpringBootTest
public class BookCommentServiceImplTest {

    private final BookCommentServiceImpl bookCommentService;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BookCommentServiceImplTest(BookCommentServiceImpl bookCommentService, MongoTemplate mongoTemplate) {
        this.bookCommentService = bookCommentService;
        this.mongoTemplate = mongoTemplate;
    }

    @BeforeEach
    public void beforeEach() {
        mongoTemplate.remove(new Query(), BookComment.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), BookComment.class));
        mongoTemplate.remove(new Query(), Book.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), Book.class));
        mongoTemplate.remove(new Query(), Author.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), Author.class));
        mongoTemplate.remove(new Query(), Genre.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), Genre.class));
        mongoTemplate.insert(getAuthors(), Author.class);
        mongoTemplate.insert(getGenres(), Genre.class);
        mongoTemplate.insert(getBooks(), Book.class);
        mongoTemplate.insert(getBookComments(), BookComment.class);
    }

    @AfterEach
    public void afterEach() {
        mongoTemplate.remove(new Query(), BookComment.class);
        mongoTemplate.remove(new Query(), Book.class);
        mongoTemplate.remove(new Query(), Author.class);
        mongoTemplate.remove(new Query(), Genre.class);
    }

    @DisplayName("should return the correct number of 'BookComments' by 'Book' id")
    @ParameterizedTest
    @MethodSource("getBooks")
    void countByBookIdTestCase1(Book book) {
        long expected = mongoTemplate.find(new Query(), BookComment.class).stream()
                .filter(bc -> bc.getBook().equals(book)).count();
        assertThat(bookCommentService.findByBookId(book.getId()).size()).isEqualTo(expected);
    }

    @DisplayName("should remove all 'BookComments' by 'Book' id")
    @ParameterizedTest
    @MethodSource("getBooks")
    void deleteByBookIdTestCase1(Book book) {
        List<BookComment> actual = mongoTemplate.find(new Query(), BookComment.class).stream()
                .filter(bc -> bc.getBook().equals(book)).toList();
        assertThat(actual).isNotEmpty();
        bookCommentService.deleteByBookId(book.getId());
        actual = mongoTemplate.find(new Query(), BookComment.class).stream()
                .filter(bc -> bc.getBook().equals(book)).toList();
        assertThat(actual).isEmpty();
    }

    @DisplayName("should remove 'BookComments' by id")
    @ParameterizedTest
    @MethodSource("getBookComments")
    void deleteByIdTestCase1(BookComment bookComment) {
        assertThat(mongoTemplate.findById(bookComment.getId(), BookComment.class)).isEqualTo(bookComment);
        bookCommentService.deleteById(bookComment.getId());
        assertThat(mongoTemplate.findById(bookComment.getId(), BookComment.class)).isNull();
    }

    @DisplayName("should find all 'BookComments' by 'Book' id")
    @ParameterizedTest
    @MethodSource("getBooks")
    void findByBookIdTestCase1(Book book) {
        var expected = getBookComments().stream()
                .filter(bc -> bc.getBook().equals(book)).toList();
        var actual = bookCommentService.findByBookId(book.getId());
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should find all 'BookComment' by id")
    @ParameterizedTest
    @MethodSource("getBookComments")
    void findByIdTestCase1(BookComment expected) {
        var actual = bookCommentService.findById(expected.getId());
        assertThat(actual).isPresent().get()
                .isEqualTo(expected);
    }

    @DisplayName("should throw an Exception because the 'BookComment' not found by id")
    @Test
    void findByIdOrThrowTestCase1() {
        assertThatThrownBy(() -> bookCommentService.findByIdOrThrow("book_comment_id_dummy"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("should insert a new 'BookComment' correctly")
    @ParameterizedTest
    @MethodSource("getBooks")
    void insertTestCase1(Book book) {
        var expected = bookCommentService.insert(book.getId(), "book_comment_text_test");
        assertThat(mongoTemplate.findById(expected.getId(), BookComment.class)).isEqualTo(expected);
    }

    @DisplayName("should convert 'BookComment' to DTO correctly")
    @ParameterizedTest
    @MethodSource("getBookComments")
    void toDtoTestCase1(BookComment bookComment) {
        BookCommentDto expected = new BookCommentDto(bookComment.getId(),
                bookComment.getUuid(),
                bookComment.getBook().getId(),
                bookComment.getText());
        assertThat(bookCommentService.toDto(bookComment)).isEqualTo(expected);
    }

    @DisplayName("should update 'BookComment' correctly")
    @ParameterizedTest
    @MethodSource("getBookComments")
    void saveTestCase1(BookComment expected) {
        expected = mongoTemplate.findById(expected.getId(), BookComment.class);
        assertThat(expected).isNotNull();
        expected = bookCommentService.update(expected.getId(), expected.getBook().getId(), "book_comment_new_text");
        assertThat(mongoTemplate.findById(expected.getId(), BookComment.class)).isEqualTo(expected);
    }

    //
//    @BeforeEach
//    public void beforeEach() {
//        bookCommentDtos = getBookCommentDtos();
//    }
//
//    @DisplayName("Должен загружать Комментарий книги по ID")
//    @ParameterizedTest
//    @MethodSource("getBookCommentDtos")
//    void shouldReturnCorrectById(BookCommentDto bookCommentDto) {
//        assertThat(bookCommentService.findById(bookCommentDto.id()))
//                .isPresent().contains(bookCommentDto);
//    }
//
//    @DisplayName("Должен загрузить список Комментариев по ID книг")
//    @ParameterizedTest
//    @MethodSource("getBookIds")
//    void shouldReturnCorrectBookCommentsByBookId(Long bookId) {
//        List<BookCommentDto> expectedBookCommentsList = getBookCommentDtosByBookId(bookId);
//        List<BookCommentDto> actualBookCommentsList = bookCommentService.findByBook(bookId);
//        assertThat(actualBookCommentsList).isNotNull()
//                .isNotEmpty()
//                .containsExactlyElementsOf(expectedBookCommentsList);
//    }
//
//    @DisplayName("Должен корректно сохранить новые Комментарии к книгам")
//    @ParameterizedTest
//    @MethodSource("getNewBookCommentDtos")
//    void shouldInsertNewBookComment(BookCommentDto expectedBookCommentDto) {
//        assertThat(bookCommentService.findById(expectedBookCommentDto.id()))
//                .isEmpty();
//        BookCommentDto returnedBookCommentDto = bookCommentService
//                .insert(expectedBookCommentDto.bookId(), expectedBookCommentDto.text());
//        assertThat(returnedBookCommentDto).isNotNull()
//                .matches(dto -> dto.id() > 0)
//                .usingRecursiveComparison()
//                .ignoringExpectedNullFields()
//                .isEqualTo(expectedBookCommentDto);
//        assertThat(bookCommentService.findById(returnedBookCommentDto.id()))
//                .isPresent().contains(expectedBookCommentDto);
//    }
//
//    @DisplayName("Должен корректно сохранить измененные Комментарии")
//    @ParameterizedTest
//    @MethodSource("getChangedBookCommentDtos")
//    void shouldUpdateBookComment(BookCommentDto expectedBookCommentDto) {
//        assertThat(bookCommentService.findById(expectedBookCommentDto.id()))
//                .isNotEqualTo(Optional.of(expectedBookCommentDto));
//        BookCommentDto returnedBookComment = bookCommentService.update(expectedBookCommentDto.id(),
//                expectedBookCommentDto.bookId(), expectedBookCommentDto.text());
//        assertThat(returnedBookComment).isEqualTo(expectedBookCommentDto);
//        assertThat(bookCommentService.findById(expectedBookCommentDto.id()))
//                .isEqualTo(Optional.of(expectedBookCommentDto));
//    }
//
//    @DisplayName("Должен удалить Комментарий по ID")
//    @ParameterizedTest
//    @MethodSource("getBookCommentDtos")
//    void shouldDeleteBookCommentById(BookCommentDto bookCommentDto) {
//        assertThat(bookCommentService.findById(bookCommentDto.id())).isEqualTo(Optional.of(bookCommentDto));
//        bookCommentService.deleteById(bookCommentDto.id());
//        assertThat(bookCommentService.findById(bookCommentDto.id())).isNotEqualTo(Optional.of(bookCommentDto));
//    }
//
//    @DisplayName("Должен удалить все Комментарий по ID книги")
//    @ParameterizedTest
//    @MethodSource("getBookIds")
//    void shouldDeleteBookCommentById(Long bookId) {
//        assertThat(bookCommentService.findByBook(bookId))
//                .containsExactlyElementsOf(getBookCommentDtosByBookId(bookId));
//        bookCommentService.deleteAllByBookId(bookId);
//        assertThat(bookCommentService.findByBook(bookId))
//                .isEmpty();
//    }
//
//    private static List<Long> getBookIds() {
//        return LongStream.range(1, 4).boxed().toList();
//    }
//
//    private static List<BookCommentDto> getBookCommentDtos() {
//        List<BookCommentDto> bookCommentDtos = new ArrayList<>();
//        long idBookComment = 1;
//        for (Long idBook = 1L; idBook < 4L; idBook++) {
//            for (int i = 1; i < 4; i++) {
//                bookCommentDtos.add(new BookCommentDto(idBookComment++, idBook, "Book_%s_comment_%s".formatted(idBook, i)));
//            }
//        }
//        return bookCommentDtos;
//    }
//
//    private static List<BookCommentDto> getChangedBookCommentDtos() {
//        List<BookCommentDto> bookCommentDtos = new ArrayList<>();
//        long idBookComment = 1;
//        for (Long idBook = 3L; idBook > 0; idBook--) {
//            for (int i = 3; i > 0; i--) {
//                bookCommentDtos.add(new BookCommentDto(idBookComment++, idBook, "Changed_Book_%s_comment_%s".formatted(idBook, i)));
//            }
//        }
//        return bookCommentDtos;
//    }
//
//    private static List<BookCommentDto> getNewBookCommentDtos() {
//        AtomicLong bookCommentId = new AtomicLong(9L);
//        return getBookIds().stream().map(bookId ->
//                        new BookCommentDto(bookCommentId.incrementAndGet(), bookId, "New_text_comment_" + bookId))
//                .toList();
//    }
//
//    private static List<BookCommentDto> getBookCommentDtosByBookId(Long bookId) {
//        return bookCommentDtos.stream().filter(e -> e.bookId() == bookId).toList();
//
//    }
    private static List<Author> getAuthors() {
        return IntStream.range(0, 5).boxed()
                .map(id -> new Author("author_id_" + id, "author_uuid_" + id, "Author_" + id))
                .toList();
    }

    private static List<Book> getBooks() {
        List<Author> authors = getAuthors();
        List<Genre> genres = getGenres();
        return IntStream.range(0, 5).boxed()
                .map(id ->
                        new Book("book_id_" + id, "book_uuid_" + id, "book_title_" + id,
                                authors.get(id),
                                List.of(genres.get(id % genres.size()), genres.get((id + 1) % genres.size()))))
                .toList();
    }

    private static List<BookComment> getBookComments() {
        List<Book> books = getBooks();
        List<BookComment> bookComments = new ArrayList<>();
        int bookCommentId = 0;
        for (Book book : books) {
            int commentNumber = 0;
            while (commentNumber < 3) {
                bookComments.add(new BookComment(
                        "book_comment_id_" + bookCommentId,
                        "book_comment_uuid_" + bookCommentId, book,
                        "book_comment_text_" + bookCommentId));
                commentNumber++;
                bookCommentId++;
            }
        }
        return bookComments;
    }

    private static List<Genre> getGenres() {
        return IntStream.range(0, 5).boxed()
                .map(id -> new Genre("genre_id_" + id, "genre_uuid_" + id, "genre_name_" + id))
                .toList();
    }

}

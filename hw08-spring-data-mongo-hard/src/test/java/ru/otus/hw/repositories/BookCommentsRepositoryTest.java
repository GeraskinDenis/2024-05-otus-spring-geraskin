package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с комментариями книг ")
@DataJpaTest
//@Import({BookCommentsRepository.class})
class BookCommentsRepositoryTest {

    @Autowired
    private BookCommentsRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать комментарий к книге по id")
    @ParameterizedTest
    @MethodSource("getDbBookComments")
    void shouldReturnCorrectBookById(BookComment expectedBookComment) {
        var actualBookComment = repository.findById(expectedBookComment.getId());
        assertThat(actualBookComment).isPresent().get()
                .isEqualTo(expectedBookComment);
    }


    @DisplayName("должен загружать список всех комментариев к ID книге")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookCommentsListByBookId(Book book) {
        var actualBookComments = repository.findByBook(book);
        var expectedBookComments = book.getComments();
        assertThat(actualBookComments).containsExactlyElementsOf(expectedBookComments);
        actualBookComments.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новый комментарий")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldSaveNew(Book book) {
        var expectedBookComment = new BookComment(0, book, "Book_%s_test_comment".formatted(book.getId()));
        var returnedBookComment = repository.save(expectedBookComment);
        assertThat(returnedBookComment).isNotNull()
                .matches(c -> c.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBookComment);
        assertThat(em.find(BookComment.class, returnedBookComment.getId()))
                .isEqualTo(returnedBookComment);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @ParameterizedTest
    @MethodSource("getDbBookComments")
    void shouldSaveUpdatedBookComment(BookComment bookComment) {
        BookComment expectedBookComment = em.find(BookComment.class, bookComment.getId());
        BookComment actualBookComment = em.find(BookComment.class, expectedBookComment.getId());
        assertThat(actualBookComment).isEqualTo(expectedBookComment);
        expectedBookComment.setText("New comment text");
        var returnedBookComment = repository.save(expectedBookComment);
        assertThat(returnedBookComment).isNotNull()
                .matches(c -> c.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBookComment);
        assertThat(em.find(BookComment.class, returnedBookComment.getId()))
                .isEqualTo(expectedBookComment);
    }

    @DisplayName("должен удалить комментарий по ID")
    @ParameterizedTest
    @MethodSource("getDbBookComments")
    void shouldDeleteBookCommentById(BookComment bookComment) {
        assertThat(em.find(BookComment.class, bookComment.getId())).isNotNull().isEqualTo(bookComment);
        repository.deleteById(bookComment.getId());
        assertThat(em.find(BookComment.class, bookComment.getId())).isNull();
    }

    @DisplayName("должен удалять все комментарии по ID книги")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void shouldDeleteAllBookCommentsByBookId(long bookId) {
        Book actualBook = em.find(Book.class, bookId);
        List<BookComment> actualBookComments = actualBook.getComments();
        assertThat(actualBookComments).isNotNull()
                .matches(list -> !list.isEmpty());
        repository.deleteByBookId(bookId);
        em.flush();
        em.detach(actualBook);
        actualBook = em.find(Book.class, bookId);
        actualBookComments = actualBook.getComments();
        assertThat(actualBookComments).isNotNull()
                .matches(List::isEmpty);
    }

    @DisplayName("should return the correct number of book comments by book id")
    @Test
    void countByBookIdTestCase1() {
        getNumberBookCommentsByBookId().forEach((key, value) ->
                assertThat(repository.countByBookId(key)).isEqualTo(value));
    }

    private static List<BookComment> getDbBookComments() {
        List<Author> dbAuthors = getDbAuthors();
        List<Genre> dbGenres = getDbGenres();
        List<Book> dbBooks = getDbBooks(dbAuthors, dbGenres);
        return getDbBookComments(dbBooks);
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id ->
                        new Book(id, "BookTitle_" + id, dbAuthors.get(id - 1),
                                dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)))
                .toList();
    }

    private static List<Book> getDbBooks() {
        List<Author> dbAuthors = getDbAuthors();
        List<Genre> dbGenres = getDbGenres();
        List<Book> dbBooks = getDbBooks(dbAuthors, dbGenres);
        getDbBookComments(dbBooks);
        return dbBooks;
    }

    private static List<BookComment> getDbBookComments(List<Book> dbBooks) {
        List<BookComment> bookComments = new ArrayList<>();
        int bookCommentId = 0;
        for (Book book : dbBooks) {
            int commentNumber = 0;
            List<BookComment> list = new ArrayList<>();
            while (commentNumber < 3) {
                BookComment bookComment = new BookComment(++bookCommentId, book, "Book_%s_comment_%s".formatted(book.getId(), ++commentNumber));
                list.add(bookComment);
                bookComments.add(bookComment);
            }
            book.setComments(list);
        }
        return bookComments;
    }

    private static Map<Long, Integer> getNumberBookCommentsByBookId() {
        Map<Long, Integer> numberBookCommentsByBookId = new HashMap<>(3);
        numberBookCommentsByBookId.put(1L, 3);
        numberBookCommentsByBookId.put(2L, 3);
        numberBookCommentsByBookId.put(3L, 3);
        numberBookCommentsByBookId.put(4L, 0);
        return numberBookCommentsByBookId;
    }

    private static List<Long> getBookIds() {
        return List.of(1L, 2L, 3L);
    }
}
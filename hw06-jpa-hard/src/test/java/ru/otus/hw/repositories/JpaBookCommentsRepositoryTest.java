package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с комментариями книг ")
@DataJpaTest
@Import({JpaBookCommentsRepository.class})
class JpaBookCommentsRepositoryTest {

    @Autowired
    private JpaBookCommentsRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("должен загружать комментарий к книге по id")
    @ParameterizedTest
    @MethodSource("getDbBookComments")
    void shouldReturnAllBookCommentsByBookId(BookComment expectedBookComment) {
        var actualBookComment = repository.findById(expectedBookComment.getId());
        assertThat(actualBookComment).isPresent().get()
                .isEqualTo(expectedBookComment);
    }


    @DisplayName("должен загружать список всех комментариев к ID книге")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookCommentsListByBookId(Book book) {
        var actualBookComments = repository.findAllByBookId(book.getId());
        var expectedBookComments = book.getComments();
        assertThat(actualBookComments).containsExactlyElementsOf(expectedBookComments);
        actualBookComments.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новый комментарий")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldSaveNewBookComment(Book book) {
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
    void shouldUpdatedBookComment(BookComment bookComment) {
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
    void shouldDeleteAllBookCommentsByBookId(int bookId) {
        Book actualBook = em.find(Book.class, bookId);
        List<BookComment> actualBookComments = actualBook.getComments();
        assertThat(actualBookComments).isNotNull()
                .matches(list -> !list.isEmpty());
        // Удаляем комментарии по ИД книги
        repository.deleteAllByBookId(bookId);
        // т.к. сессия открыта, то моментальное удаление в БД не происходит
        // удаление произойдет после выхода из метода и перед закрытием сессии с БД

        // Синхронизируем изменения с БД, запускаем отложенные запросы
        em.flush();
        // Запросы выполнены, в БД все удалилось, но книга все еще в кеше

        // Отключаем книгу от кэша
        em.detach(actualBook);

        // Получаю книгу из БД заново
        actualBook = em.find(Book.class, bookId);
        // т.к. мы в транзакции, то получаем все комментарии книги из LAZY-поля
        actualBookComments = actualBook.getComments();
        // Выполняем проверку наличия Комментариев
        assertThat(actualBookComments).isNotNull()
                .matches(List::isEmpty);
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
                                dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2), null))
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

    private static List<Integer> getBookIds() {
        return List.of(1, 2, 3);
    }
}
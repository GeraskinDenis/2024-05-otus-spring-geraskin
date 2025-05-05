package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

// В этом тесте не используется аннотация `@DataJpaTest`,
// потому что данная аннотация отключает Liquibase
@DisplayName("Репозиторий на основе JPA для работы с книгами ")
@SpringBootTest // поднимает Контекст приложения
// по умолчанию загружается основная конфигурация `src/main/resource/application.yml`
// следующая аннотация заменяет / добавляет значения
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb", "spring.shell.interactive.enabled=false"})
@AutoConfigureTestEntityManager // добавляем `org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager`,
// который не доступен с аннотацией `@SpringBootTest`, а доступен с аннотацией `@DataJpaTest`
@Transactional // Откатывает изменения после теста
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("should delete book by id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void deleteByIdTestCase1(Book book) {
        assertThat(em.find(Book.class, book.getId()))
                .isNotNull()
                .isEqualTo(book);
        repository.deleteById(book.getId());
        assertThat(em.find(Book.class, book.getId())).isNull();
    }

    @DisplayName("should return the correct list of books")
    @Test
    void findAllTestCase1() {
        var actualBooks = repository.findAll();
        var expectedBooks = getDbBooks();
        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("should return the correct book by id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void findByIdTestCase1(Book expectedBook) {
        assertThat(repository.findById(expectedBook.getId()))
                .isPresent().get().isEqualTo(expectedBook);
    }

    @DisplayName("should find the book with max id")
    @Test
    void findWithMaxIdTestCase1() {
        assertThat(repository.findWithMaxId()).isNotEmpty().map(b -> assertThat(b.getId()).isEqualTo(5));
    }

    @DisplayName("should save the new book correctly")
    @Test
    void saveTestCase1() {
        var expectedBook = new Book(0, "BookTitle_10500", em.find(Author.class, 1),
                List.of(em.find(Genre.class, 1), em.find(Genre.class, 2)));
        var returnedBook = repository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
        assertThat(em.find(Book.class, returnedBook.getId()))
                .isEqualTo(expectedBook);
    }

    @DisplayName("should update the existing book correctly")
    @Test
    void saveTestCase2() {
        var expectedBook = new Book(1L, "BookTitle_10500", em.find(Author.class, 2),
                List.of(em.find(Genre.class, 4), em.find(Genre.class, 5)));
        assertThat(em.find(Book.class, expectedBook.getId()))
                .isNotEqualTo(expectedBook);
        var returnedBook = repository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
        assertThat(em.find(Book.class, returnedBook.getId()))
                .isEqualTo(expectedBook);
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
        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "BookTitle_1", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1))));
        books.add(new Book(2, "BookTitle_2", dbAuthors.get(1), List.of(dbGenres.get(2), dbGenres.get(3))));
        books.add(new Book(3, "BookTitle_3", dbAuthors.get(2), List.of(dbGenres.get(4), dbGenres.get(5))));
        books.add(new Book(4, "BookTitle_4", dbAuthors.get(2), List.of(dbGenres.get(0), dbGenres.get(5))));
        books.add(new Book(5, "BookTitle_5", dbAuthors.get(2), List.of(dbGenres.get(1), dbGenres.get(3))));
        return books;
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}
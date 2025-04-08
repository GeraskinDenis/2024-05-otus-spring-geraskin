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
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с книгами ")
@DataJpaTest
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
        assertThat(repository.findWithMaxId()).isNotEmpty().map(b -> assertThat(b.getId()).isEqualTo(3));
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
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)))
                .toList();
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}
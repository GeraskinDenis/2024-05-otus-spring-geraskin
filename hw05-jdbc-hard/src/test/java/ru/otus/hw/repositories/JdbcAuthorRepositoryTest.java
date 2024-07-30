package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с 'Авторами'")
@JdbcTest
@Import(JdbcAuthorRepository.class)
public class JdbcAuthorRepositoryTest {

    @Autowired
    JdbcAuthorRepository authorRepository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @DisplayName("should return the correct list of Authors")
    @Test
    void findAllTest() {
        List<Author> actual = authorRepository.findAll();
        List<Author> expected = dbAuthors;
        assertThat(actual).containsExactlyElementsOf(expected);
        actual.forEach(System.out::println);
    }

    @DisplayName("should return the correct Author by 'id'")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void findByIdTestCase1(Author expected) {
        Optional<Author> actual = authorRepository.findById(expected.getId());
        assertThat(actual).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("should not find the author by 'id'")
    @Test
    void findByIdTestCase2() {
        Optional<Author> actual = authorRepository.findById(10L);
        assertThat(actual).isEmpty();
    }

    @DisplayName("should save a new Author correctly")
    @Test
    void insertTest() {
        Author expected = authorRepository.save(new Author(0, "TestAuthor"));
        assertThat(expected).isNotNull()
                .matches(e -> e.getId() > 0)
                .matches(e -> Objects.nonNull(e.getFullName()))
                .matches(e -> !e.getFullName().isEmpty());
        Optional<Author> actual = authorRepository.findById(expected.getId());
        assertThat(actual).isPresent().get()
                .isEqualTo(expected);
    }

    @DisplayName("should delete Author by 'id'")
    @Test
    void deleteByIdTest() {
        assertThat(authorRepository.findById(1L)).isPresent();
        authorRepository.deleteById(1L);
        assertThat(authorRepository.findById(1L)).isEmpty();

    }

    @DisplayName("should update Author correctly")
    @Test
    void updateTest() {
        Author expected = authorRepository.findById(1L).get();
        int expectedCount = authorRepository.findAll().size();
        expected.setFullName("NewFullNameTest");
        authorRepository.save(expected);
        assertThat(expectedCount).isEqualTo(authorRepository.findAll().size());
        Author actual = authorRepository.findById(1L).get();
        assertThat(actual).isEqualTo(expected);
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }
}

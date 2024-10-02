package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с 'Авторами'")
@DataJpaTest
@Import(JpaAuthorRepository.class)
public class JpaAuthorRepositoryTest {

    @Autowired
    private JpaAuthorRepository repository;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @DisplayName("should return the correct list of Authors")
    @Test
    void findAllTest() {
        List<Author> actual = repository.findAll();
        List<Author> expected = dbAuthors;
        assertThat(actual).containsExactlyElementsOf(expected);
        actual.forEach(System.out::println);
    }

    @DisplayName("should return the correct Author by 'id'")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void findByIdTestCase1(Author expected) {
        Optional<Author> actual = repository.findById(expected.getId());
        assertThat(actual).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("should not find the author by 'id'")
    @Test
    void findByIdTestCase2() {
        Optional<Author> actual = repository.findById(10L);
        assertThat(actual).isEmpty();
    }

    @DisplayName("should save a new Author correctly")
    @Test
    void insertTest() {
        Author expected = repository.save(new Author(0, "TestAuthor"));
        assertThat(expected).isNotNull()
                .matches(e -> e.getId() > 0)
                .matches(e -> Objects.nonNull(e.getFullName()))
                .matches(e -> !e.getFullName().isEmpty());
        Author actual = em.find(Author.class, expected.getId());
        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @DisplayName("should delete Author by 'id'")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void deleteByIdTest(Author author) {
        assertThat(em.find(Author.class, author.getId()))
                .isNotNull().isEqualTo(author);
        repository.deleteById(author.getId());
        assertThat(em.find(Author.class, author.getId()))
                .isNull();

    }

    @DisplayName("should update Author correctly")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void updateTest(Author author) {
        Author actual = em.find(Author.class, author.getId());
        assertThat(actual).isNotNull().isEqualTo(author);
        actual.setFullName("NewFullNameTest");
        actual = repository.save(actual);
        assertThat(actual).isNotNull()
                .isNotEqualTo(author);
        assertThat(em.find(Author.class, author.getId()))
                .isNotNull().isEqualTo(actual);
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 5).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }
}

package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с 'Жанрами'")
@DataJpaTest
@Import(GenreRepository.class)
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("should return the correct list of Genres")
    @Test
    void findAllTest() {
        List<Genre> actual = repository.findAll();
        List<Genre> expected = getDbGenres();
        assertThat(actual).containsExactlyElementsOf(expected);
        actual.forEach(System.out::println);
    }

    @DisplayName("should return the correct Genre by 'id'")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void findByIdTestCase1(Genre expected) {
        Optional<Genre> actual = repository.findById(expected.getId());
        assertThat(actual).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("should not find the Genre by 'id'")
    @Test
    void findByIdTestCase2() {
        Optional<Genre> actual = repository.findById(10L);
        assertThat(actual).isEmpty();
    }

    @DisplayName("should save a new Genre correctly")
    @Test
    void insertTest() {
        Genre expected = repository.save(new Genre(0, "TestGenre"));
        assertThat(expected).isNotNull()
                .matches(e -> e.getId() > 0)
                .matches(e -> Objects.nonNull(e.getName()))
                .matches(e -> !e.getName().isEmpty());
        Genre actual = em.find(Genre.class, expected.getId());
        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @DisplayName("should delete Genre by 'id'")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void deleteByIdTest(Genre genre) {
        assertThat(em.find(Genre.class, genre.getId()))
                .isNotNull().isEqualTo(genre);
        repository.deleteById(genre.getId());
        assertThat(em.find(Genre.class, genre.getId()))
                .isNull();
    }

    @DisplayName("should update Genre correctly")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void updateTest(Genre genre) {
        Genre actual = em.find(Genre.class, genre.getId());
        assertThat(actual).isNotNull().isEqualTo(genre);
        actual.setName("NewNameTest");
        actual = repository.save(actual);
        assertThat(actual).isNotNull()
                .isNotEqualTo(genre);
        assertThat(em.find(Genre.class, genre.getId()))
                .isNotNull().isEqualTo(actual);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}

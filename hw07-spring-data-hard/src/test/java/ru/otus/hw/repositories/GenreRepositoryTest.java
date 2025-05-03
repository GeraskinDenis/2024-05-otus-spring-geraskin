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

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с 'Жанрами'")
@DataJpaTest
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("should delete Genre by 'id'")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void deleteByIdTestCase1(Genre genre) {
        assertThat(em.find(Genre.class, genre.getId()))
                .isNotNull().isEqualTo(genre);
        repository.deleteById(genre.getId());
        assertThat(em.find(Genre.class, genre.getId())).isNull();
    }

    @DisplayName("should return the correct list of Genres")
    @Test
    void findAllTestCase1() {
        List<Genre> actual = repository.findAll();
        List<Genre> expected = getDbGenres();
        assertThat(actual).containsExactlyElementsOf(expected);
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

    @DisplayName("should get correctly the number of books by genres")
    @Test
    void getNumberOfBooksByGenreTestCase1() {
        List<Map<String, Object>> expected = getNumberOfBooksByGenre();
        List<Map<String, Object>> actual = repository.getNumberOfBooksByGenre();
        assertThat(actual.size()).isEqualTo(expected.size());
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("should save a new Genre correctly")
    @Test
    void saveTestCase1() {
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

    @DisplayName("should update a Genre correctly")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void saveTestCase2(Genre genre) {
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
                .map(id -> new Genre(id, "Genre_" + id)).toList();
    }

    private static List<Map<String, Object>> getNumberOfBooksByGenre() {
        List<Map<String, Object>> expected = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            Map<String, Object> row = new HashMap<>(3);
            row.put("ID", (long) i);
            row.put("NAME", "Genre_" + i);
            row.put("NUMBER", 1L);
            expected.add(row);
        }
        return expected;
    }
}

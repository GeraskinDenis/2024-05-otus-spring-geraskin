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
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.projections.NumberOfBooksByGenre;

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

// В этом тесте не используется аннотация `@DataJpaTest`,
// потому что данная аннотация отключает Liquibase
@DisplayName("Репозиторий на основе JPA для работы с 'Жанрами'")
@SpringBootTest // поднимает Контекст приложения
// по умолчанию загружается основная конфигурация `src/main/resource/application.yml`
// следующая аннотация заменяет / добавляет значения
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb", "spring.shell.interactive.enabled=false"})
@AutoConfigureTestEntityManager // добавляем `org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager`,
// который не доступен с аннотацией `@SpringBootTest`, а доступен с аннотацией `@DataJpaTest`
@Transactional // Откатывает изменения после теста
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

    @DisplayName("Should return the correct Genre by ID")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void findByIdTestCase1(Genre expected) {
        var actual = repository.findById(expected.getId());
        assertThat(actual).contains(expected);
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
        List<NumberOfBooksByGenre> expected = getNumberOfBooksByGenre();
        List<NumberOfBooksByGenre> actual = repository.countBooksByGenres();
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

    private static List<NumberOfBooksByGenre> getNumberOfBooksByGenre() {
        List<NumberOfBooksByGenre> expected = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            expected.add(new NumberOfBooksByGenreImpl(i, "Genre_" + i, (i == 3 || i == 5) ? 1 : 2));
        }
        return expected;
    }

    private record NumberOfBooksByGenreImpl(long id, String name, int number) implements NumberOfBooksByGenre {

        @Override
        public long getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getNumber() {
            return number;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof NumberOfBooksByGenre that)) return false;
            return id == that.getId() && number == that.getNumber() && Objects.equals(name, that.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, number);
        }
    }
}

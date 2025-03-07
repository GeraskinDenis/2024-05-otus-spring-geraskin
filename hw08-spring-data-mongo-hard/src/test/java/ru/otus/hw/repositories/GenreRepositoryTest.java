package ru.otus.hw.repositories;

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
import ru.otus.hw.models.Genre;

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("The test set for 'Genre' repository")
@SpringBootTest
public class GenreRepositoryTest {

    private final GenreRepository repository;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public GenreRepositoryTest(GenreRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @BeforeEach
    public void beforeEach() {
        mongoTemplate.remove(new Query(), Genre.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), Genre.class));
        mongoTemplate.insert(getGenres(), Genre.class);
    }

    @AfterEach
    public void afterEach() {
        mongoTemplate.remove(new Query(), Genre.class);
    }


    @DisplayName("should delete 'Genre' by id")
    @ParameterizedTest
    @MethodSource("getGenres")
    void deleteByIdTestCase1(Genre genre) {
        assertThat(mongoTemplate.findById(genre.getId(), Genre.class)).isEqualTo(genre);
        repository.deleteById(genre.getId());
        assertThat(mongoTemplate.findById(genre.getId(), Genre.class)).isNull();
    }

    @DisplayName("should find all 'Genres'")
    @Test
    void findAllTestCase1() {
        List<Genre> expected = getGenres();
        List<Genre> actual = repository.findAll();
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should find 'Genre' by id")
    @ParameterizedTest
    @MethodSource("getGenres")
    void findByIdTestCase1(Genre expected) {
        Optional<Genre> actual = repository.findById(expected.getId());
        assertThat(actual).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("should find no 'Genre' by id")
    @Test
    void findByIdTestCase2() {
        Optional<Genre> actual = repository.findById("genre_id_dummy");
        assertThat(actual).isEmpty();
    }

    @DisplayName("should find 'Genre' by name substring")
    @ParameterizedTest
    @MethodSource("getGenres")
    void findByNameLikeTestCase1(Genre expected) {
        assertThat(repository.findByNameLike(expected.getName())).contains(expected);
    }

    @DisplayName("should find all 'Genre' by name substring")
    @Test
    void findByNameLikeTestCase2() {
        assertThat(repository.findByNameLike("genre_name_")).containsExactlyElementsOf(getGenres());
    }

    @DisplayName("should save a new 'Genre' correctly")
    @Test
    void saveTestCase1() {
        Genre expected = repository.save(new Genre("genre_id_test", "genre_uuid_test", "genre_name_test"));
        assertThat(expected).isEqualTo(mongoTemplate.findById(expected.getId(), Genre.class));
    }

    @DisplayName("should update a 'Genre' correctly")
    @ParameterizedTest
    @MethodSource("getGenres")
    void saveTestCase2(Genre genre) {
        Genre expected = mongoTemplate.findById(genre.getId(), Genre.class);
        assertThat(expected).isNotNull();
        expected = new Genre(expected.getId(), expected.getUuid(), "genre_new_name_test");
        expected = repository.save(expected);
        assertThat(mongoTemplate.findById(expected.getId(), Genre.class)).isEqualTo(expected);
    }

    private static List<Genre> getGenres() {
        return IntStream.range(0, 5).boxed()
                .map(id -> new Genre("genre_id_" + id, "genre_uuid_" + id, "genre_name_" + id))
                .toList();
    }
}

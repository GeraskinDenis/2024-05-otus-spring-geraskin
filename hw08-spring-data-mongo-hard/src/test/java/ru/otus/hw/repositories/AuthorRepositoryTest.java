package ru.otus.hw.repositories;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Author;

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Test set for 'Author' repository")
@SpringBootTest
public class AuthorRepositoryTest {

    private final AuthorRepository repository;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AuthorRepositoryTest(AuthorRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @BeforeEach
    public void beforeEach() {
        mongoTemplate.remove(new Query(), Author.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), Author.class));
        mongoTemplate.insert(getAuthors(), Author.class);
    }

    @AfterEach
    public void afterEach() {
        mongoTemplate.remove(new Query(), Author.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), Author.class));
    }

    @DisplayName("should delete 'Author' by 'id'")
    @ParameterizedTest
    @MethodSource("getAuthors")
    void deleteByIdTest(Author author) {
        assertThat(mongoTemplate.findById(author.getId(), Author.class)).isNotNull().isEqualTo(author);
        repository.deleteById(author.getId());
        assertThat(mongoTemplate.findById(author.getId(), Author.class)).isNull();
    }

    @DisplayName("should return the correct list of Authors")
    @Test
    public void findAllTestCase1() {
        List<Author> actual = repository.findAll();
        List<Author> expected = getAuthors();
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should find Author by 'fullname' substring correctly")
    @ParameterizedTest
    @MethodSource("getAuthors")
    public void findByFullNameLikeTestCase1(Author author) {
        assertThat(repository.findByFullNameLike(author.getFullName())).hasSize(1).first().isEqualTo(author);
    }

    @DisplayName("should find all Authors by 'fullname' substring")
    @Test
    public void findByFullNameLikeTestCase2() {
        List<Author> expected = getAuthors();
        assertThat(repository.findByFullNameLike("author_fullname_")).containsExactlyElementsOf(expected);
    }

    @DisplayName("should return the correct Author by 'id'")
    @ParameterizedTest
    @MethodSource("getAuthors")
    void findByIdTestCase1(Author expected) {
        Optional<Author> actual = repository.findById(expected.getId());
        assertThat(actual).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("should not find the author by 'id'")
    @Test
    void findByIdTestCase2() {
        Optional<Author> actual = repository.findById("dummy_id");
        assertThat(actual).isEmpty();
    }

    @DisplayName("should save a new Author correctly")
    @Test
    void saveTestCase1() {
        final var expected = repository.save(
                new Author("author_id_test", "author_uuid_test", "author_full_name_test"));
        repository.save(expected);
        assertThat(mongoTemplate.findById(expected.getId(), Author.class)).isNotNull()
                .matches(a -> a.getId().equals(expected.getId()));
    }

    @DisplayName("should update 'Author 'correctly")
    @ParameterizedTest
    @MethodSource("getAuthors")
    void saveTestCase2(Author author) {
        final var expected = mongoTemplate.findById(author.getId(), Author.class);
        assertThat(expected).isNotNull();
        expected.setFullName("author_fullname_test");
        repository.save(expected);
        assertThat(mongoTemplate.findById(author.getId(), Author.class)).isEqualTo(expected)
                .matches(a -> a.getId().equals(expected.getId()));
    }

    private static List<Author> getAuthors() {
        return IntStream.range(1, 5).boxed()
                .map(id -> new Author("author_id_" + id, "author_uuid_" + id, "author_fullname_" + id))
                .toList();
    }
}

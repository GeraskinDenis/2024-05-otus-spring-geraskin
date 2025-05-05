package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.projections.NumberOfBooksByAuthor;

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

// В этом тесте не используется аннотация `@DataJpaTest`,
// потому что данная аннотация отключает Liquibase
@DisplayName("Репозиторий на основе JPA для работы с 'Авторами'")
@SpringBootTest // поднимает Контекст приложения
// по умолчанию загружается основная конфигурация `src/main/resource/application.yml`
// следующая аннотация заменяет значения указанных параметров для выполнения тестов
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb", "spring.shell.interactive.enabled=false"})
@AutoConfigureTestEntityManager // добавляем `org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager`,
// который не доступен с аннотацией `@SpringBootTest`, а доступен с аннотацией `@DataJpaTest`
@Transactional // Откатывает изменения после теста
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository repository;

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

    @DisplayName("should return the correct number of books by author")
    @Test
    void getNumberOfBooksByAuthors() {
        // Данная реализация мне не очень нравиться. Метод equals() для сравнения объектов не подходит, т.к. разные Классы.
        // Был вариант использовать Map для проверки, но отталкивала мысль о том, что Проекция может иметь больше двух полей
        // Так же старался, что бы реализация содержала как можно меньше кода, что бы его тоже не пришлось тестировать.
        // Но кода получилось много для тестового метода, но он понятный и простой.
        List<NumberOfBooksByAuthor> expected = getListOfNumberOfBooksByAuthors();
        List<NumberOfBooksByAuthor> actual = repository.countBooksByAuthors();
        assertThat(actual.size()).isEqualTo(expected.size());
        for (NumberOfBooksByAuthor a : actual) {
            boolean exists = false;
            for (NumberOfBooksByAuthor e : expected) {
                if (a.getAuthorFullName().equals(e.getAuthorFullName())) {
                    exists = true;
                    assertThat(a.getNumber()).isEqualTo(e.getNumber());
                }
            }
            assertThat(exists).isTrue();
        }
        for (NumberOfBooksByAuthor e : expected) {
            boolean exists = false;
            for (NumberOfBooksByAuthor a : actual) {
                if (a.getAuthorFullName().equals(e.getAuthorFullName())) {
                    exists = true;
                    assertThat(a.getNumber()).isEqualTo(e.getNumber());
                }
            }
            assertThat(exists).isTrue();
        }
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

    private List<NumberOfBooksByAuthor> getListOfNumberOfBooksByAuthors() {
        Map<String, Integer> expectedData = new HashMap<>(3);
        expectedData.put("Author_1", 1);
        expectedData.put("Author_2", 1);
        expectedData.put("Author_3", 3);
        return expectedData.entrySet().stream().map(e -> {
            NumberOfBooksByAuthor mockEntity = Mockito.mock(NumberOfBooksByAuthor.class);
            Mockito.when(mockEntity.getAuthorFullName()).thenReturn(e.getKey());
            Mockito.when(mockEntity.getNumber()).thenReturn(e.getValue());
            return mockEntity;
        }).toList();
    }

}

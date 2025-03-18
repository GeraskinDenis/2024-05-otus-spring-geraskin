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
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Test set for 'BookComment' repository")
@SpringBootTest
class BookCommentsRepositoryTest {

    private final MongoTemplate mongoTemplate;

    private final BookCommentsRepository repository;

    @Autowired
    public BookCommentsRepositoryTest(MongoTemplate mongoTemplate, BookCommentsRepository repository) {
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
    }

    @BeforeEach
    public void beforeEach() {
        mongoTemplate.remove(new Query(), BookComment.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), BookComment.class));
        mongoTemplate.remove(new Query(), Book.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), Book.class));
        mongoTemplate.remove(new Query(), Author.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), Author.class));
        mongoTemplate.remove(new Query(), Genre.class);
        assertThat(0).isEqualTo(mongoTemplate.count(new Query(), Genre.class));
        mongoTemplate.insert(getAuthors(), Author.class);
        mongoTemplate.insert(getGenres(), Genre.class);
        mongoTemplate.insert(getBooks(), Book.class);
        mongoTemplate.insert(getBookComments(), BookComment.class);
    }

    @AfterEach
    public void afterEach() {
        mongoTemplate.remove(new Query(), BookComment.class);
        mongoTemplate.remove(new Query(), Book.class);
        mongoTemplate.remove(new Query(), Author.class);
        mongoTemplate.remove(new Query(), Genre.class);
    }

    @DisplayName("should return the correct number of book comments by book id")
    @ParameterizedTest
    @MethodSource("getBooks")
    void countByBookIdTestCase1(Book book) {
        long expected = mongoTemplate.find(new Query(), BookComment.class).stream()
                .filter(bc -> bc.getBook().equals(book)).count();
        assertThat(repository.findByBookId(book.getId()).size()).isEqualTo(expected);
    }

    @DisplayName("should remove all book comment by book id")
    @ParameterizedTest
    @MethodSource("getBooks")
    void deleteByBookIdTestCase1(Book book) {
        List<BookComment> actual = mongoTemplate.find(new Query(), BookComment.class).stream()
                .filter(bc -> bc.getBook().equals(book)).toList();
        assertThat(actual).isNotEmpty();
        repository.deleteByBookId(book.getId());
        actual = mongoTemplate.find(new Query(), BookComment.class).stream()
                .filter(bc -> bc.getBook().equals(book)).toList();
        assertThat(actual).isEmpty();
    }

    @DisplayName("should remove book comment by id")
    @ParameterizedTest
    @MethodSource("getBookComments")
    void deleteByIdTestCase1(BookComment bookComment) {
        assertThat(mongoTemplate.findById(bookComment.getId(), BookComment.class)).isEqualTo(bookComment);
        repository.deleteById(bookComment.getId());
        assertThat(mongoTemplate.findById(bookComment.getId(), BookComment.class)).isNull();
    }

    @DisplayName("should find all 'BookComments' by 'Book' id")
    @ParameterizedTest
    @MethodSource("getBooks")
    void findByBookIdTestCase1(Book book) {
        var expected = getBookComments().stream()
                .filter(bc -> bc.getBook().equals(book)).toList();
        var actual = repository.findByBookId(book.getId());
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should find 'BookComment' by id")
    @ParameterizedTest
    @MethodSource("getBookComments")
    void findByIdTestCase1(BookComment expected) {
        var actual = repository.findById(expected.getId());
        assertThat(actual).isPresent().get()
                .isEqualTo(expected);
    }

    @DisplayName("should save a new 'BookComment' correctly")
    @ParameterizedTest
    @MethodSource("getBooks")
    void saveTestCase1(Book book) {
        var expected = new BookComment("book_comment_id_test", "book_comment_uuid_test",
                book, "book_comment_text_test");
        expected = repository.save(expected);
        assertThat(mongoTemplate.findById(expected.getId(), BookComment.class)).isEqualTo(expected);
    }

    @DisplayName("should update book comment correctly")
    @ParameterizedTest
    @MethodSource("getBookComments")
    void saveTestCase2(BookComment expected) {
        expected = mongoTemplate.findById(expected.getId(), BookComment.class);
        assertThat(expected).isNotNull();
        expected = new BookComment(expected.getId(), expected.getUuid(), expected.getBook(),
                "book_comment_new_text");
        expected = repository.save(expected);
        var actual = mongoTemplate.findById(expected.getId(), BookComment.class);
        assertThat(actual).isEqualTo(expected);
    }

    private static List<Author> getAuthors() {
        return IntStream.range(0, 5).boxed()
                .map(id -> new Author("author_id_" + id, "author_uuid_" + id, "Author_" + id))
                .toList();
    }

    private static List<Book> getBooks() {
        List<Author> authors = getAuthors();
        List<Genre> genres = getGenres();
        return IntStream.range(0, 5).boxed()
                .map(id ->
                        new Book("book_id_" + id, "book_uuid_" + id, "book_title_" + id,
                                authors.get(id),
                                List.of(genres.get(id % genres.size()), genres.get((id + 1) % genres.size()))))
                .toList();
    }

    private static List<BookComment> getBookComments() {
        List<Book> books = getBooks();
        List<BookComment> bookComments = new ArrayList<>();
        int bookCommentId = 0;
        for (Book book : books) {
            int commentNumber = 0;
            while (commentNumber < 3) {
                bookComments.add(new BookComment(
                        "book_comment_id_" + bookCommentId,
                        "book_comment_uuid_" + bookCommentId, book,
                        "book_comment_text_" + bookCommentId));
                commentNumber++;
                bookCommentId++;
            }
        }
        return bookComments;
    }

    private static List<Genre> getGenres() {
        return IntStream.range(0, 5).boxed()
                .map(id -> new Genre("genre_id_" + id, "genre_uuid_" + id, "genre_name_" + id))
                .toList();
    }
}
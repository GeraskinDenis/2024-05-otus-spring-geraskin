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
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Test set for 'Book' repository")
@SpringBootTest
class BookRepositoryTest {

    private final BookRepository repository;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BookRepositoryTest(BookRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
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

    @DisplayName("should delete book by id")
    @ParameterizedTest
    @MethodSource("getBooks")
    void deleteByIdTestCase1(Book book) {
        assertThat(mongoTemplate.findById(book.getId(), Book.class)).isEqualTo(book);
        repository.deleteById(book.getId());
        assertThat(mongoTemplate.findById(book.getId(), Book.class)).isNull();
    }

    @DisplayName("should return the correct list of books")
    @Test
    void findAllTestCase1() {
        assertThat(repository.findAll()).containsExactlyElementsOf(getBooks());
    }


    @DisplayName("should find books by author list correctly")
    @ParameterizedTest
    @MethodSource("getAuthors")
    void findByAuthorInTestCase1(Author author) {
        List<Book> expected = getBooks().stream().filter(book -> book.getAuthor().equals(author)).toList();
        List<Book> actual = repository.findByAuthorIn(List.of(author.getId()));
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should find all books by all author ids correctly")
    @Test
    void findByAuthorInTestCase2() {
        List<String> authorIds = getAuthors().stream().map(Author::getId).toList();
        List<Book> expected = getBooks();
        List<Book> actual = repository.findByAuthorIn(authorIds);
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should find 'Books' by 'Genre' ids")
    @ParameterizedTest
    @MethodSource("getGenres")
    public void findByGenresInTestCase1(Genre genre) {
        List<Book> expected = getBooks().stream().filter(book -> book.getGenres().contains(genre)).toList();
        List<Book> actual = repository.findByGenresIn(List.of(genre.getId()));
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should find all books by all genre ids")
    @Test
    public void findByGenresInTestCase2() {
        List<Book> expected = getBooks();
        List<Book> actual = repository.findByGenresIn(getGenres().stream().map(Genre::getId).toList());
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should find book by id correct")
    @ParameterizedTest
    @MethodSource("getBooks")
    void findByIdTestCase1(Book book) {
        assertThat(repository.findById(book.getId())).isPresent().get().isEqualTo(book);
    }

    @DisplayName("should find all 'Books' by title substring")
    @ParameterizedTest
    @MethodSource("getBooks")
    public void findByTitleLikeTestCase1(Book expected){
        assertThat(repository.findByTitleLike(expected.getTitle())).contains(expected);
    }

    @DisplayName("should find all 'Books' by title substring")
    @Test
    public void findByTitleLikeTestCase2(){
        List<Book> expected = getBooks();
        List<Book> actual = repository.findByTitleLike("book_title_");
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should save the new book correctly")
    @Test
    void saveTestCase1() {
        var genres = mongoTemplate.find(new Query(), Genre.class);
        var authors = mongoTemplate.find(new Query(), Author.class);
        var expected = new Book("book_id_test", "book_uuid_test", "book_title_test", authors.get(0),
                List.of(genres.get(0), genres.get(1)));
        expected = repository.save(expected);
        assertThat(mongoTemplate.findById(expected.getId(), Book.class)).isEqualTo(expected);
    }

    @DisplayName("should update the existing book correctly")
    @ParameterizedTest
    @MethodSource("getBooks")
    void saveTestCase2(Book book) {
        var genres = mongoTemplate.find(new Query(), Genre.class);
        var authors = mongoTemplate.find(new Query(), Author.class);
        var expected = mongoTemplate.findById(book.getId(), Book.class);
        assertThat(expected).isNotNull();
        expected = new Book(book.getId(), book.getUuid(), "book_title_test", authors.get(0),
                List.of(genres.get(0), genres.get(1)));
        expected = repository.save(expected);
        assertThat(mongoTemplate.findById(expected.getId(), Book.class)).isEqualTo(expected);
    }

    private static List<Author> getAuthors() {
        return IntStream.range(0, 5).boxed()
                .map(id -> new Author("author_id_" + id, "author_uuid_" + id, "author_fullname_" + id))
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
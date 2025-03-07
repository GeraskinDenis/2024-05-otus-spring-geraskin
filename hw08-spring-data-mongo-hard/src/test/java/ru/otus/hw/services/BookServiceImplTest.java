package ru.otus.hw.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.*;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Test set for 'BookServiceImpl'")
@SpringBootTest
public class BookServiceImplTest {

    private final BookServiceImpl bookService;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BookServiceImplTest(BookServiceImpl bookService, MongoTemplate mongoTemplate) {
        this.bookService = bookService;
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
        bookService.deleteById(book.getId());
        assertThat(mongoTemplate.findById(book.getId(), Book.class)).isNull();
    }

    @DisplayName("should return the correct list of books")
    @Test
    void findAllTestCase1() {
        assertThat(bookService.findAll()).containsExactlyElementsOf(getBooks());
    }

    @DisplayName("should find all books by Author full name substring")
    @ParameterizedTest
    @MethodSource("getAuthorFullNameSubstring")
    public void findByAuthorFullNameSubstringTestCase1(String fullNameSubstring) {
        List<Book> expected = getBooks().stream()
                .filter(book -> book.getAuthor()
                        .getFullName()
                        .toLowerCase()
                        .contains(fullNameSubstring.toLowerCase())
                ).toList();
        List<Book> actual = bookService.findByAuthorFullNameSubstring(fullNameSubstring);
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should find book by id correct")
    @ParameterizedTest
    @MethodSource("getBooks")
    void findByIdTestCase1(Book book) {
        assertThat(bookService.findById(book.getId())).isPresent().get().isEqualTo(book);
    }

    @DisplayName("should throw an Exception because the 'Book' was not found by id")
    @Test
    void findByIdTestCase() {
        assertThatThrownBy(() -> bookService.findByIdOrThrow("book_id_dummy")).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("should find all 'Books' by title substring")
    @ParameterizedTest
    @MethodSource("getBooks")
    public void findByTitleSubstringTestCase1(Book expected) {
        assertThat(bookService.findByTitleSubstring(expected.getTitle())).contains(expected);
    }

    @DisplayName("should find all books by title substring")
    @Test
    public void findByTitleSubstringTestCase2() {
        List<Book> expected = getBooks();
        List<Book> actual = bookService.findByTitleSubstring("book_title_");
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("should insert the new book correctly")
    @Test
    void insertTestCase1() {
        var genres = mongoTemplate.find(new Query(), Genre.class);
        var authors = mongoTemplate.find(new Query(), Author.class);
        var expected = bookService.insert("book_title_test", authors.get(0).getId(),
                Set.of(genres.get(0).getId(), genres.get(1).getId()));
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
        expected = bookService.update(book.getId(), "book_title_test", authors.get(0).getId(),
                Set.of(genres.get(0).getId(), genres.get(1).getId()));
        assertThat(mongoTemplate.findById(expected.getId(), Book.class)).isEqualTo(expected);
    }

    @DisplayName("should convert 'Book' to DTO correctly")
    @ParameterizedTest
    @MethodSource("getBooks")
    void toDtoTestCase1(Book book) {
        Author author = book.getAuthor();
        AuthorDto authorDto = new AuthorDto(author.getId(), author.getUuid(), author.getFullName());
        List<GenreDto> genreDtos = book.getGenres().stream()
                .map(genre -> new GenreDto(genre.getId(), genre.getUuid(), genre.getName())).toList();
        BookDto expected = new BookDto(book.getId(), book.getUuid(), book.getTitle(), authorDto, genreDtos);
        BookDto actual = bookService.toDto(book);
        assertThat(actual).isEqualTo(expected);
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

    private static List<String> getAuthorFullNameSubstring() {
        List<String> subStrings = IntStream.range(0, 5).boxed().map(id -> "author_fullname_" + id)
                .collect(Collectors.toList());
        subStrings.add("hor_full");
        subStrings.add("author");
        subStrings.add("fullname");
        subStrings.add("dummy_full_name");
        return subStrings;
    }

}

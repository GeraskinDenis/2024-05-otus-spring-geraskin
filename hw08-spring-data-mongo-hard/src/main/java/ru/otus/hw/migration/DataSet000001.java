package ru.otus.hw.migration;

import io.mongock.api.annotations.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@ChangeUnit(id = "dataset-000001", order = "000001", author = "Geraskin Denis")
public class DataSet000001 {

    private final int NUMBER_OF_ENTITIES = 10;

    private final MongoTemplate mongoTemplate;

    public DataSet000001(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @BeforeExecution
    public void before() {
        mongoTemplate.createCollection(Author.class);
        mongoTemplate.createCollection(Genre.class);
        mongoTemplate.createCollection(Book.class);
        mongoTemplate.createCollection(BookComment.class);
    }

    @RollbackBeforeExecution
    public void rollbackBefore() {
        mongoTemplate.dropCollection(Author.class);
        mongoTemplate.dropCollection(Genre.class);
        mongoTemplate.dropCollection(Book.class);
        mongoTemplate.dropCollection(BookComment.class);
    }

    @Execution
    public void execution() {
        List<Author> authors = (List<Author>) mongoTemplate.insert(IntStream.range(0, NUMBER_OF_ENTITIES).boxed()
                .map(this::getAuthor).toList(), Author.class);
        List<Genre> genres = (List<Genre>) mongoTemplate.insert(IntStream.range(0, NUMBER_OF_ENTITIES).boxed()
                .map(this::getGenre).toList(), Genre.class);
        List<Book> books = (List<Book>) mongoTemplate.insert(getBooks(authors, genres), Book.class);
        mongoTemplate.insert(getBooksComments(books), BookComment.class);
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.remove(Author.class);
        mongoTemplate.remove(Genre.class);
        mongoTemplate.remove(Book.class);
        mongoTemplate.remove(BookComment.class);
    }

    private Author getAuthor(int i) {
        return new Author(UUID.randomUUID().toString(), "Author_" + i);
    }

    private Book getBook(int i, Author author, List<Genre> genres) {
        return new Book(UUID.randomUUID().toString(), "The book title " + i, author, genres);
    }

    private List<Book> getBooks(List<Author> authors, List<Genre> genres) {
        return IntStream.range(0, authors.size()).boxed()
                .map(i -> getBook(i, authors.get(i), getBookGenres(i, genres))).toList();
    }

    private List<BookComment> getBookComments(Book book) {
        return IntStream.range(0, 3).boxed()
                .map(i -> new BookComment(UUID.randomUUID().toString(), book, "The book comment text" + i))
                .toList();
    }

    private List<BookComment> getBooksComments(List<Book> books) {
        List<BookComment> booksComments = new ArrayList<>();
        books.forEach(book -> booksComments.addAll(getBookComments(book)));
        return booksComments;
    }

    private List<Genre> getBookGenres(int i, List<Genre> genres) {
        if (i < 2) {
            return List.of(genres.get(0), genres.get(1));
        } else if (i < 5) {
            return List.of(genres.get(2), genres.get(3));
        } else if (i < 7) {
            return List.of(genres.get(4), genres.get(5));
        } else if (i < 9) {
            return List.of(genres.get(6), genres.get(7));
        } else {
            return List.of(genres.get(8), genres.get(9));
        }
    }

    private Genre getGenre(int i) {
        return new Genre(UUID.randomUUID().toString(), "Genre_" + i);
    }


}

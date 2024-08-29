package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        String sql = """
                SELECT
                    book.id AS book_id,
                    book.title AS book_title,
                    author.id AS author_id,
                    author.full_name AS author_full_name,
                    genre.id AS genre_id,
                    genre.name AS genre_name
                FROM
                    books AS book
                    INNER JOIN authors AS author ON book.author_id = author.id
                    INNER JOIN books_genres AS books_genres ON book.id = books_genres.book_id
                    INNER JOIN genres AS genre ON books_genres.genre_id = genre.id
                WHERE
                    book.id = :id
                """;
        List<Book> books = jdbcOperations.query(sql, Collections.singletonMap("id", id), new BookResultSetExtractor());
        if (Objects.isNull(books) || books.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(books.get(0));
    }

    @Override
    public List<Book> findAll() {
        String sql = """
                SELECT
                    book.id AS book_id,
                    book.title AS book_title,
                    author.id AS author_id,
                    author.full_name AS author_full_name,
                    genre.id AS genre_id,
                    genre.name AS genre_name
                FROM
                    books AS book
                    INNER JOIN authors AS author ON book.author_id = author.id
                    INNER JOIN books_genres AS books_genres ON book.id = books_genres.book_id
                    INNER JOIN genres AS genre ON books_genres.genre_id = genre.id
                """;
        return jdbcOperations.query(sql, new BookResultSetExtractor());
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM books WHERE id = :id";
        jdbcOperations.update(sql, Map.of("id", id));
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = "SELECT book_id, genre_id FROM books_genres";
        return jdbcOperations.query(sql, new BookGenreRelationRowMapper());
    }

    private Book insert(Book book) {
        if (book.getId() > 0) {
            return update(book);
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = """
                INSERT INTO books (title, author_id) VALUES (:title, :author_id)
                """;
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());
        jdbcOperations.update(sql, paramMap, keyHolder, new String[]{"id"});
        Long id = Optional.ofNullable(keyHolder.getKeyAs(Long.class)).orElseThrow();
        book.setId(id);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        String sql = """
                UPDATE books SET title = :title, author_id = :author_id WHERE id = :id
                """;
        Map<String, Object> paramMap = Map.of(
                "title", book.getTitle(),
                "author_id", book.getAuthor().getId(),
                "id", book.getId());
        if (jdbcOperations.update(sql, paramMap) == 0) {
            throw new EntityNotFoundException("Book not found by ID: " + book.getId());
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        String sql = "INSERT INTO books_genres (book_id, genre_id) VALUES (:bookId, :genreId)";
        List<BookGenreRelation> relations = book.getGenres()
                .stream()
                .map(g -> new BookGenreRelation(book.getId(), g.getId()))
                .toList();
        jdbcOperations.batchUpdate(sql, SqlParameterSourceUtils.createBatch(relations));
    }

    private void removeGenresRelationsFor(Book book) {
        String sql = "DELETE FROM books_genres WHERE book_id = :book_id";
        jdbcOperations.update(sql, Collections.singletonMap("book_id", book.getId()));
    }

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<List<Book>> {

        @Override
        public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Book> books = new HashMap<>();
            Map<Long, Author> authors = new HashMap<>();
            Map<Long, Genre> genres = new HashMap<>();
            while (rs.next()) {
                Long bookId = rs.getLong("book_id");
                Long genreId = rs.getLong("genre_id");
                Long authorId = rs.getLong("author_id");
                Author author = authors.get(authorId);
                if (Objects.isNull(author)) {
                    author = new Author(authorId, rs.getString("author_full_name"));
                    authors.put(authorId, author);
                }
                Genre genre = genres.get(genreId);
                if (Objects.isNull(genre)) {
                    genre = new Genre(genreId, rs.getString("genre_name"));
                    genres.put(genreId, genre);
                }
                Book book = books.get(bookId);
                if (Objects.isNull(book)) {
                    book = new Book(bookId, rs.getString("book_title"), author, new ArrayList<>());
                    book.setGenres(new ArrayList<>());
                    books.put(bookId, book);
                }
                book.getGenres().add(genre);
            }
            return new ArrayList<>(books.values());
        }
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"));
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}

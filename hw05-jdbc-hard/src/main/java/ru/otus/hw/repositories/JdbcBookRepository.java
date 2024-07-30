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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Book book = jdbcOperations.query(sql, Collections.singletonMap("id", id), new BookResultSetExtractor());
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        List<Genre> genres = genreRepository.findAll();
        List<BookGenreRelation> relations = getAllGenreRelations();
        List<Book> books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
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

    private List<Book> getAllBooksWithoutGenres() {
        String sql = """
                SELECT
                    book.id AS book_id,
                    book.title AS book_title,
                    author.id AS author_id,
                    author.full_name AS author_full_name
                FROM
                    books AS book
                    LEFT JOIN authors AS author ON book.author_id = author.id
                """;
        return jdbcOperations.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = "SELECT book_id, genre_id FROM books_genres";
        return jdbcOperations.query(sql, new BookGenreRelationRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres, List<BookGenreRelation> relations) {
        Map<Long, Book> bookMap = booksWithoutGenres.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));
        Map<Long, Genre> genreMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));
        booksWithoutGenres.forEach(b -> b.setGenres(new ArrayList<>()));
        for (BookGenreRelation relation : relations) {
            Optional.ofNullable(bookMap.get(relation.bookId))
                    .orElseThrow(() -> new EntityNotFoundException("Book not found by ID:" + relation.bookId))
                    .getGenres()
                    .add(Optional.ofNullable(genreMap.get(relation.genreId()))
                            .orElseThrow(() -> new EntityNotFoundException("Genre not found by ID:" + relation.bookId)));
        }
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
        Long id = Objects.requireNonNull(keyHolder.getKeyAs(Long.class));
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

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setId(rs.getLong("book_id"));
            book.setTitle(rs.getString("book_title"));
            book.setAuthor(new Author(rs.getLong("author_id"),
                    rs.getString("author_full_name")));
            return book;
        }
    }

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            long bookId = 0;
            String bookTitle = "";
            long authorId = 0;
            String authorFullName = "";
            List<Genre> genres = new ArrayList<>();
            while (rs.next()) {
                bookId = rs.getLong("book_id");
                bookTitle = rs.getString("book_title");
                authorId = rs.getLong("author_id");
                authorFullName = rs.getString("author_full_name");
                genres.add(new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
            }
            if (bookId == 0) {
                return null;
            } else {
                return new Book(bookId, bookTitle, new Author(authorId, authorFullName), genres);
            }
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

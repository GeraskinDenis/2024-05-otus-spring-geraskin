package ru.otus.hw.repositories;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BookResultSetExtractor implements ResultSetExtractor<List<Book>> {

    private Map<Long, Book> books = new HashMap<>();

    private Map<Long, Author> authors = new HashMap<>();

    private Map<Long, Genre> genres = new HashMap<>();

    @Override
    public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {

        while (rs.next()) {
            Book book = getBook(rs);
            Genre genre = getGenre(rs);
            book.getGenres().add(genre);
        }
        return new ArrayList<>(books.values());
    }

    private Genre getGenre(ResultSet rs) throws SQLException {
        Long genreId = rs.getLong("genre_id");
        Genre genre = genres.get(genreId);
        if (Objects.isNull(genre)) {
            genre = new GenreRowMapper().mapRow(rs, 0);
            genres.put(genreId, genre);
        }
        return genre;
    }

    private Author getAuthor(ResultSet rs) throws SQLException {
        Long authorId = rs.getLong("author_id");
        Author author = authors.get(authorId);
        if (Objects.isNull(author)) {
            author = new AuthorRowMapper().mapRow(rs, 0);
            authors.put(authorId, author);
        }
        return author;
    }

    private Book getBook(ResultSet rs) throws SQLException {
        Long bookId = rs.getLong("book_id");
        Book book = books.get(bookId);
        if (Objects.isNull(book)) {
            book = new Book(bookId, rs.getString("book_title"), getAuthor(rs), new ArrayList<>());
            books.put(bookId, book);
        }
        return book;
    }
}

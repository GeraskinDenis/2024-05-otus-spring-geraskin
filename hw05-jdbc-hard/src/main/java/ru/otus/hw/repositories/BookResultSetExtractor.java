package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
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

@Component
@RequiredArgsConstructor
public class BookResultSetExtractor implements ResultSetExtractor<List<Book>> {

    private final AuthorRowMapper authorRowMapper;

    private final GenreRowMapper genreRowMapper;

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
                author = authorRowMapper.mapRow(rs, 0);
                authors.put(authorId, author);
            }
            Genre genre = genres.get(genreId);
            if (Objects.isNull(genre)) {
                genre = genreRowMapper.mapRow(rs, 0);
                genres.put(genreId, genre);
            }
            Book book = books.get(bookId);
            if (Objects.isNull(book)) {
                book = new Book(bookId, rs.getString("book_title"), author, new ArrayList<>());
                books.put(bookId, book);
            }
            book.getGenres().add(genre);
        }
        return new ArrayList<>(books.values());
    }
}

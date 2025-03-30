package ru.otus.hw.repositories;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.hw.models.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int i) throws SQLException {
        long id = rs.getLong("book_id");
        String title = rs.getString("book_title");
        return new Book(id, title, null, new ArrayList<>());
    }
}

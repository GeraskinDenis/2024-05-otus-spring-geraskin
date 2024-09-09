package ru.otus.hw.repositories;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorRowMapper implements RowMapper<Author> {

    @Override
    public Author mapRow(ResultSet rs, int i) throws SQLException {
        long id = rs.getLong("author_id");
        String fullName = rs.getString("author_full_name");
        return new Author(id, fullName);
    }
}

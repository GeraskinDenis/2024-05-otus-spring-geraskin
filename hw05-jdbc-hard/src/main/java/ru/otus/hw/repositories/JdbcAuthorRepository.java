package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public List<Author> findAll() {
        String sql = """
                SELECT
                    id,
                    full_name
                FROM authors
                """;
        return jdbcOperations.query(sql, new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        String sql = """
                SELECT
                    id,
                    full_name
                FROM authors
                WHERE id = :id
                """;
        try {
            return Optional.ofNullable(jdbcOperations.queryForObject(sql,
                    Collections.singletonMap("id", id),
                    new AuthorRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == 0) {
            return insert(author);
        } else {
            return update(author);
        }
    }

    @Override
    public void deleteById(long id) {
        if (id == 0) {
            return;
        }
        String sql = "DELETE FROM authors WHERE id = :id";
        SqlParameterSource sqlParameter = new MapSqlParameterSource("id", id);
        jdbcOperations.update(sql, sqlParameter);
    }

    private Author insert(Author author) {
        String sql = "INSERT INTO authors (full_name) VALUES (:full_name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource paramMap = new MapSqlParameterSource("full_name", author.getFullName());
        jdbcOperations.update(sql, paramMap, keyHolder, new String[]{"id"});
        author.setId(keyHolder.getKeyAs(Long.class));
        return author;
    }

    private Author update(Author author) {
        String sql = "UPDATE authors SET full_name = :full_name WHERE id = :id";
        MapSqlParameterSource paramMap = new MapSqlParameterSource("full_name", author.getFullName())
                .addValue("id", author.getId());
        jdbcOperations.update(sql, paramMap);
        return author;
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String fullName = rs.getString("full_name");
            return new Author(id, fullName);
        }
    }
}

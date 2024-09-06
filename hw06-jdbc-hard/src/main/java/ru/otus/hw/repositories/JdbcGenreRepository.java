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
import ru.otus.hw.exceptions.JdbcDataIntegrityViolationException;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT id, name FROM genres ";
        return jdbcOperations.query(sql, new GenreRowMapper());
    }

    @Override
    public Optional<Genre> findById(Long id) {
        String sql = "SELECT id, name FROM genres WHERE id = :id ";
        try {
            return Optional.ofNullable(
                    jdbcOperations.queryForObject(sql,
                            Collections.singletonMap("id", id),
                            new GenreRowMapper())
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> findByIds(Set<Long> ids) {
        String sql = "SELECT id, name FROM genres WHERE id IN (:id)";
        return jdbcOperations.query(sql, Collections.singletonMap("id", ids), new GenreRowMapper());
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == 0) {
            return insert(genre);
        } else {
            return update(genre);
        }
    }

    @Override
    public void deleteById(long id) {
        if (id == 0) {
            return;
        }
        String sql = "DELETE FROM genres WHERE id = :id";
        SqlParameterSource sqlParameter = new MapSqlParameterSource("id", id);
        jdbcOperations.update(sql, sqlParameter);
    }

    private Genre insert(Genre genre) {
        String sql = "INSERT INTO genres (name) VALUES (:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource paramMap = new MapSqlParameterSource("name", genre.getName());
        jdbcOperations.update(sql, paramMap, keyHolder, new String[]{"id"});
        Long id = Optional.ofNullable(keyHolder.getKeyAs(Long.class))
                .orElseThrow(() -> new JdbcDataIntegrityViolationException("The genre is not saved in the DB."));
        genre.setId(id);
        return genre;
    }

    private Genre update(Genre genre) {
        String sql = "UPDATE genres SET name = :name WHERE id = :id";
        MapSqlParameterSource paramMap = new MapSqlParameterSource("name", genre.getName())
                .addValue("id", genre.getId());
        jdbcOperations.update(sql, paramMap);
        return genre;
    }

    private static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}

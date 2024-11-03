package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Map;

public interface GenreReport {
    @Query(value = """
            SELECT
                genre.id,
                genre.name,
                count(book_genre.genre_id) AS number
            FROM genres AS genre
                LEFT JOIN books_genres AS book_genre ON genre.id = book_genre.genre_id
            GROUP BY
                genre.id,
                genre.name
            """, nativeQuery = true)
    List<Map<String, Object>> getNumberOfBooksByGenre();
}

package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.repositories.projections.NumberOfBooksByGenre;

import java.util.List;

public interface GenreRepositoryReport {
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
    List<NumberOfBooksByGenre> countBooksByGenres();

    @Query(value = """
            SELECT
                genre.id,
                genre.name,
                count(book_genre.genre_id) AS number
            FROM genres AS genre
                LEFT JOIN books_genres AS book_genre ON genre.id = book_genre.genre_id
            WHERE genre.id IN (:_genre_ids)
            GROUP BY
                genre.id,
                genre.name
            """, nativeQuery = true)
    List<NumberOfBooksByGenre> countBooksByGenres(@Param("_genre_ids") List<Long> genreIds);
}

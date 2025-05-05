package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.repositories.projections.NumberOfBooksByAuthor;

import java.util.List;

public interface AuthorRepositoryReports {
    @Query("""
            SELECT
                a.fullName AS authorFullName,
                count(b.author) AS number
            FROM
                Author AS a
                JOIN Book AS b ON a = b.author
            GROUP BY a.fullName
            """)
    List<NumberOfBooksByAuthor> countBooksByAuthors();

    @Query("""
            SELECT
                a.fullName AS authorFullName,
                count(b.author) AS number
            FROM
                Author AS a
                JOIN Book AS b ON a = b.author
            WHERE a.id IN (:_author_ids)
            GROUP BY
                a.fullName
            """)
    List<NumberOfBooksByAuthor> countBooksByAuthors(@Param("_author_ids") List<Long> authorIds);
}

package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.projections.NumberOfBooksByAuthor;

import java.util.List;

public interface AuthorRepository extends ListCrudRepository<Author, Long> {
    @Query("""
            SELECT
                a.fullName AS authorFullName,
                count(b.author) AS number
            FROM Author AS a
            JOIN FETCH Book AS b ON a = b.author
            GROUP BY a.fullName""")
    List<NumberOfBooksByAuthor> getNumberOfBooksByAuthors();
}

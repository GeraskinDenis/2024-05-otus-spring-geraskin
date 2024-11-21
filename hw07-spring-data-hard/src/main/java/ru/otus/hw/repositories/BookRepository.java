package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends ListCrudRepository<Book, Long> {

    @Override
    @EntityGraph(attributePaths = {"author"})
    List<Book> findAll();

    @EntityGraph(attributePaths = {"author"})
    Optional<Book> findById(long id);

    @EntityGraph(attributePaths = {"author"})
    List<Book> findByAuthorFullNameLike(String authorFullNameSubstring);

    @Query(value = """
            SELECT
                book
            FROM Book AS book
            ORDER BY book.id DESC
            LIMIT 1
            """)
    Optional<Book> findWithMaxId();

    @EntityGraph(attributePaths = {"author"})
    List<Book> findByTitleLike(String titleSubstring);
}

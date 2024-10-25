package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
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
}

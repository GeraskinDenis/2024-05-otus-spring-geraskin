package ru.otus.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends ListCrudRepository<Book, String> {

    List<Book> findByAuthorFullNameLike(String authorFullNameSubstring);

    Optional<Book> findWithMaxId();

    List<Book> findByTitleLike(String titleSubstring);
}

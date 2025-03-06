package ru.otus.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Book;

import java.util.List;

public interface BookRepository extends ListCrudRepository<Book, String> {

    List<Book> findByAuthorFullNameLike(String fullNameSubstring);

    List<Book> findByTitleLike(String titleSubstring);
}

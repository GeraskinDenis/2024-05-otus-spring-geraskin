package ru.otus.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Book;

public interface BookRepository extends ListCrudRepository<Book, Long> {
}

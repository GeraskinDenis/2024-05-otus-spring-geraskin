package ru.otus.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Author;

public interface AuthorRepository extends ListCrudRepository<Author, String> {
}

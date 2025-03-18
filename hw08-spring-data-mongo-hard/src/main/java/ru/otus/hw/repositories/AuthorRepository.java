package ru.otus.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Author;

import java.util.List;

public interface AuthorRepository extends ListCrudRepository<Author, String> {
    List<Author> findByFullNameLike(String fullNameSubstring);
}

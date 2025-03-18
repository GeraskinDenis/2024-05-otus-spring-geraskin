package ru.otus.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Book;

import java.util.List;

public interface BookRepository extends ListCrudRepository<Book, String> {

    List<Book> findByAuthorIn(List<String> authorIds);

    List<Book> findByGenresIn(List<String> genreIds);

    List<Book> findByTitleLike(String titleSubstring);
}

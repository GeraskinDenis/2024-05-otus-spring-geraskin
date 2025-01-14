package ru.otus.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;

import java.util.List;

public interface BookCommentsRepository extends ListCrudRepository<BookComment, String> {

    int countByBookId(String bookId);

    void deleteByBookId(String bookId);

    List<BookComment> findByBook(Book book);
}

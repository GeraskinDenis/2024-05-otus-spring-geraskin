package ru.otus.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.hw.models.BookComment;

import java.util.List;

public interface BookCommentsRepository extends ListCrudRepository<BookComment, Long> {

    int countByBookId(long bookId);

    void deleteByBookId(long bookId);

    List<BookComment> findByBookId(long bookId);
}

package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookCommentsRepository implements BookCommentsRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<BookComment> findById(long id) {
        return Optional
                .ofNullable(em.find(BookComment.class, id))
                .or(Optional::empty);
    }

    @Override
    public List<BookComment> findAllByBookId(long bookId) {
        String jpql = "SELECT bc FROM BookComment bc WHERE bc.book = :book";
        return em.createQuery(jpql, BookComment.class)
                .setParameter("book", bookId)
                .getResultList();
    }

    @Override
    public BookComment save(BookComment bookComment) {
        return null;
    }

    @Override
    public void deleteById(long id) {

    }

    @Override
    public void deleteAllByBookId(long bookId) {

    }
}

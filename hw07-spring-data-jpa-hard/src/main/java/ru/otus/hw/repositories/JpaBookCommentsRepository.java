package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class JpaBookCommentsRepository implements BookCommentsRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<BookComment> findById(long id) {
        return Optional.ofNullable(em.find(BookComment.class, id));
    }

    @Override
    public List<BookComment> findAllByBookId(long bookId) {
        String jpql = "SELECT bc FROM BookComment bc WHERE bc.book.id = :bookId";
        EntityGraph<?> entityGraph = em.getEntityGraph("book_comments-book");
        return em.createQuery(jpql, BookComment.class)
                .setParameter("bookId", bookId)
                .setHint(FETCH.getKey(), entityGraph)
                .getResultList();
    }

    @Override
    public BookComment save(BookComment bookComment) {
        if (bookComment.getId() == 0) {
            em.persist(bookComment);
            return bookComment;
        }
        return em.merge(bookComment);
    }

    @Override
    public void deleteById(long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    public void deleteAllByBookId(long bookId) {
        findAllByBookId(bookId).forEach(em::remove);
    }
}

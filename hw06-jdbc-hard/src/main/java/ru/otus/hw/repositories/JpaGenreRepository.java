package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class JpaGenreRepository implements GenreRepository {

    @PersistenceContext
    private final EntityManager em;

    public JpaGenreRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Genre> findAll() {
        String jpql = "SELECT e FROM Genre e";
        return em.createQuery(jpql, Genre.class).getResultList();
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.of(em.find(Genre.class, id));
    }

    @Override
    public List<Genre> findByIds(Set<Long> ids) {
        String jpql = "SELECT e FROM Genre e WHERE e.id IN (:ids)";
        return em.createQuery(jpql, Genre.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == 0) {
            em.persist(genre);
            return genre;
        } else {
            return em.merge(genre);
        }
    }

    @Override
    public void deleteById(long id) {
        findById(id).ifPresent(em::remove);
    }
}

package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public void deleteById(String id) {
        genreRepository.deleteById(id);
    }

    @Override
    public Optional<Genre> findById(String id) {
        return genreRepository.findById(id);
    }

    @Override
    public Genre findByIdOrThrow(String id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("'Genre' not found by id: " + id));
    }

    @Override
    public List<Genre> findAllByIdOrThrow(Set<String> ids) {
        List<Genre> genres = genreRepository.findAllById(ids);
        List<String> genreIds = genres.stream().map(Genre::getId).toList();
        List<String> notFoundedIds = new ArrayList<>();
        ids.forEach(id -> {
            if (!genreIds.contains(id)) {
                notFoundedIds.add(id);
            }
        });
        if (!notFoundedIds.isEmpty()) {
            throw new EntityNotFoundException("'Genre' not found by ids: " + ids);
        }
        return genres;
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre insert(String name) {
        return genreRepository.save(new Genre(CommonUtils.getUUID(), name));
    }

    @Override
    public Genre update(String id, String name) {
        Genre genre = findByIdOrThrow(id);
        genre.setName(name);
        return genreRepository.save(genre);
    }
}

package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Override
    public Optional<GenreDto> findById(String id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if (genre.isPresent()) {
            return genre.map(genreMapper::toDto);
        }
        return Optional.empty();
    }

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(genreMapper::toDto).toList();
    }

    @Transactional
    @Override
    public GenreDto insert(String name) {
        Genre genre = new Genre(null, name);
        return genreMapper.toDto(genreRepository.save(genre));
    }

    @Transactional
    @Override
    public GenreDto update(String id, String name) {
        Genre genre = new Genre(id, name);
        return genreMapper.toDto(genreRepository.save(genre));
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        genreRepository.deleteById(id);
    }
}

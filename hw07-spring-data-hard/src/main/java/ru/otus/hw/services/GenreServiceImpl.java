package ru.otus.hw.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.Mapper;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final Mapper<Genre, GenreDto> genreMapper;

    public GenreServiceImpl(GenreRepository genreRepository,
                            @Qualifier("genreMapper") Mapper<Genre, GenreDto> genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }

    @Override
    public Optional<GenreDto> findById(long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if (genre.isPresent()) {
            return genre.map(genreMapper::toDto);
        }
        return Optional.empty();
    }

    @Override
    public List<Genre> findByIdInOrThrow(Set<Long> ids) {
        List<Genre> genres = genreRepository.findByIdIn(ids);
        if (ids.size() != genres.size()) {
            String genreIdsStr = ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
            throw new EntityNotFoundException("One or more Genres with ids [%s] not found".formatted(genreIdsStr));
        }
        return genres;
    }

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(genreMapper::toDto).toList();
    }

    @Transactional
    @Override
    public GenreDto save(long id, String name) {
        Genre genre = new Genre(id, name);
        return genreMapper.toDto(genreRepository.save(genre));
    }

    @Override
    public GenreDto toDto(Genre genre) {
        return genreMapper.toDto(genre);
    }

    @Override
    public List<GenreDto> toDto(Set<Genre> genres) {
        return genres.stream().map(genreMapper::toDto).toList();
    }

    @Override
    public Genre toObject(GenreDto genreDto) {
        return genreMapper.toObject(genreDto);
    }

    @Override
    public List<Genre> toObject(Set<GenreDto> genreDtos) {
        return genreDtos.stream().map(genreMapper::toObject).toList();
    }
}

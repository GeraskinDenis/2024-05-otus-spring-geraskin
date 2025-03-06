package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreMapperImpl implements GenreMapper {
    @Override
    public GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getUuid(), genre.getName());
    }
}

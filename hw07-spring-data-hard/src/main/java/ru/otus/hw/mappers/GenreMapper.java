package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreMapper implements Mapper<Genre, GenreDto> {

    @Override
    public GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

    @Override
    public Genre toObject(GenreDto genreDto) {
        return new Genre(genreDto.id(), genreDto.name());
    }


}

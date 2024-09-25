package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreConverter {
    public String genreToString(GenreDto genreDto) {
        return "Id: %d, Name: %s".formatted(genreDto.id(), genreDto.name());
    }
}

package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;

@Component
public class GenreConverter {
    public String genreToString(GenreDto genreDto) {
        return "Id: %s, Name: %s".formatted(genreDto.id(), genreDto.name());
    }
}

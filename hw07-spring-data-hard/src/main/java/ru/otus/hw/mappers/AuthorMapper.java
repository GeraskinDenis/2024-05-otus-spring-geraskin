package ru.otus.hw.mappers;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

public interface AuthorMapper {
    AuthorDto toDto(Author author);
}

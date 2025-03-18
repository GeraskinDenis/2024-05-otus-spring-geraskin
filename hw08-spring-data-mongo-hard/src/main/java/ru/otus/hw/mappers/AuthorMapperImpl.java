package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorMapperImpl implements AuthorMapper {

    @Override
    public AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(),
                author.getUuid(),
                author.getFullName());
    }
}

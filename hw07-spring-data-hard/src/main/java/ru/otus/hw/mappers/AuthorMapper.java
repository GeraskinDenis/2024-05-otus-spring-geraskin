package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorMapper implements Mapper<Author, AuthorDto> {

    @Override
    public AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }

    @Override
    public Author toObject(AuthorDto authorDto) {
        return new Author(authorDto.id(), authorDto.fullName());
    }
}

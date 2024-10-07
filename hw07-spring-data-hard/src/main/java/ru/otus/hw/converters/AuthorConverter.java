package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;

@Component
public class AuthorConverter {
    public String authorToString(AuthorDto authorDto) {
        return "Id: %d, FullName: %s".formatted(authorDto.id(), authorDto.fullName());
    }
}

package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    List<AuthorDto> findAll();

    Optional<AuthorDto> findById(long id);

    Author findByIdOrThrow(long id);

    void deleteById(long id);

    AuthorDto save(long id, String fullName);

    AuthorDto toDto(Author author);

    Author toObject(AuthorDto authorDto);
}

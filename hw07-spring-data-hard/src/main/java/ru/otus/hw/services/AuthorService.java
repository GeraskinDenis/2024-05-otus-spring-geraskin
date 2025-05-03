package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.Report;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    void deleteById(long id);

    List<AuthorDto> findAll();

    Optional<AuthorDto> findById(long id);

    Report getNumberOfBooksByAuthors();

    AuthorDto insert(String fullName);

    AuthorDto update(long id, String fullName);

}

package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.Report;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    void deleteById(String id);

    List<AuthorDto> findAll();

    Optional<AuthorDto> findById(String id);

    Report getNumberOfBooksByAuthors();

    AuthorDto insert(String fullName);

    AuthorDto update(String id, String fullName);

}

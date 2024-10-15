package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    public Optional<AuthorDto> findById(long id) {
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            return author.map(authorMapper::toDto);
        }
        return Optional.empty();
    }

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream().map(authorMapper::toDto).toList();
    }

    @Transactional
    @Override
    public AuthorDto insert(String fullName) {
        Author author = new Author(0, fullName);
        author = authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Transactional
    @Override
    public AuthorDto update(long id, String fullName) {
        Author author = new Author(id, fullName);
        author = authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }
}


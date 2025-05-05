package ru.otus.hw.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.Mapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final Mapper<Author, AuthorDto> authorMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository,
                             @Qualifier("authorMapper") Mapper<Author, AuthorDto> authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream().map(authorMapper::toDto).toList();
    }

    @Override
    public Optional<AuthorDto> findById(long id) {
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            return author.map(authorMapper::toDto);
        }
        return Optional.empty();
    }

    @Override
    public Author findByIdOrThrow(long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found by `id`: " + id));
    }

    @Transactional
    @Override
    public AuthorDto save(long id, String fullName) {
        Author author = new Author(id, fullName);
        author = authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Override
    public AuthorDto toDto(Author author) {
        return authorMapper.toDto(author);
    }

    @Override
    public Author toObject(AuthorDto authorDto) {
        return authorMapper.toObject(authorDto);
    }
}


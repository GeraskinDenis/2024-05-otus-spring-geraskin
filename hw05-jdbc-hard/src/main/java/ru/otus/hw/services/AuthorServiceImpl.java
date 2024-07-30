package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository repository;

    @Override
    public Optional<Author> findById(long id) {
        return repository.findById(id);
    }

    @Override
    public List<Author> findAll() {
        return repository.findAll();
    }

    @Override
    public Author insert(String fullName) {
        Author author = new Author();
        author.setFullName(fullName);
        return repository.save(author);
    }

    @Override
    public Author update(long id, String fullName) {
        return repository.save(new Author(id, fullName));
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }
}


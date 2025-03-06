package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.utils.CommonUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public void deleteById(String id) {
        authorRepository.deleteById(id);
    }

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Optional<Author> findById(String id) {
        return authorRepository.findById(id);
    }

    @Override
    public Author findByIdOrThrow(String id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("'Author' not found by id: " + id));
    }

    @Override
    public Author insert(String fullName) {
        return authorRepository.save(new Author(CommonUtils.getUUID(), fullName));
    }

    @Override
    public Author update(String id, String fullName) {
        Author author = findByIdOrThrow(id);
        author.setFullName(fullName);
        return authorRepository.save(author);
    }
}


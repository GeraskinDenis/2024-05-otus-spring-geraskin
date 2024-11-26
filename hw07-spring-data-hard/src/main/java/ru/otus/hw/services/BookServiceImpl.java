package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Transactional
    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findByAuthorFullNameLike(String authorFullNameSubstring) {
        return bookRepository.findByAuthorFullNameLike("%" + authorFullNameSubstring + "%")
                .stream().map(bookMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BookDto> findById(long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return book.map(bookMapper::toDto);
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findByTitleLike(String titleSubstring) {
        return bookRepository.findByTitleLike("%" + titleSubstring + "%")
                .stream().map(bookMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BookDto> findWithMaxId() {
        return bookRepository.findWithMaxId()
                .map(bookMapper::toDto);
    }

    @Transactional
    @Override
    public BookDto insert(String title, long authorId, Set<Long> genreIds) {
        return bookMapper.toDto(save(0, title, authorId, genreIds));
    }

    private Book save(long id, String title, long authorId, Set<Long> genreIds) {
        if (isEmpty(genreIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findByIdIn(genreIds);
        if (isEmpty(genres) || genreIds.size() != genres.size()) {
            String genreIdsStr = genreIds.stream().map(String::valueOf).collect(Collectors.joining(", "));
            throw new EntityNotFoundException("One or more genres with ids [%s] not found".formatted(genreIdsStr));
        }
        Book book;
        if (id == 0) {
            book = new Book(id, title, author, genres);
        } else {
            book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
            book.setTitle(title);
            book.setAuthor(author);
            book.setGenres(genres);
        }
        return bookRepository.save(book);
    }

    @Transactional
    @Override
    public BookDto update(long id, String title, long authorId, Set<Long> genresIds) {
        return bookMapper.toDto(save(id, title, authorId, genresIds));
    }
}

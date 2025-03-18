package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.utils.CommonUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final AuthorService authorService;

    private final GenreService genreService;

    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> findByAuthorFullNameSubstring(String fullNameSubstring) {
        List<Author> authors = authorService.findByFullName(fullNameSubstring);
        return bookRepository.findByAuthorIn(authors.stream().map(Author::getId).toList());
    }

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findByGenreNameSubstring(String nameSubstring) {
        List<Genre> genres = genreService.findByNameSubstring(nameSubstring);
        return bookRepository.findByGenresIn(genres.stream().map(Genre::getId).toList());
    }

    @Override
    public Book findByIdOrThrow(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("'Book' not found by id: " + id));
    }

    @Override
    public List<Book> findByTitleSubstring(String titleSubstring) {
        return bookRepository.findByTitleLike(titleSubstring);
    }

    @Override
    public Book insert(String title, String authorId, Set<String> genreIds) {
        return save(null, title, authorId, genreIds);
    }

    @Override
    public Book update(String id, String title, String authorId, Set<String> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    private Book save(String id, String title, String authorId, Set<String> genreIds) {
        Author author = authorService.findByIdOrThrow(authorId);
        List<Genre> genres = genreService.findAllByIdOrThrow(genreIds);
        Book book;
        if (Objects.isNull(id)) {
            book = new Book(CommonUtils.getUUID(), title, author, genres);
        } else {
            book = findByIdOrThrow(id);
            book.setTitle(title);
            book.setAuthor(author);
            book.setGenres(genres);
        }
        return bookRepository.save(book);
    }

    @Override
    public BookDto toDto(Book book) {
        return bookMapper.toDto(book);
    }
}

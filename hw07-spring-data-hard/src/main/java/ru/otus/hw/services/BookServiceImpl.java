package ru.otus.hw.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.Mapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final AuthorService authorService;

    private final GenreService genreService;

    private final BookRepository bookRepository;

    private final Mapper<Book, BookDto> bookMapper;

    public BookServiceImpl(AuthorService authorService, GenreService genreService,
                           BookRepository bookRepository, @Qualifier("bookMapper") Mapper<Book, BookDto> bookMapper) {
        this.authorService = authorService;
        this.genreService = genreService;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

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
    public Book findByIdOrThrow(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found by ID: " + id));
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
    public BookDto save(long id, String title, long authorId, Set<Long> genreIds) {
        var author = authorService.findByIdOrThrow(authorId);
        var genres = genreService.findByIdInOrThrow(genreIds);
        Book book;
        if (id == 0) {
            book = new Book(id, title, author, genres);
        } else {
            book = findByIdOrThrow(id);
            book.setTitle(title);
            book.setAuthor(author);
            book.setGenres(genres);
        }
        return bookMapper.toDto(bookRepository.save(book));
    }

    public BookDto save(BookDto bookDto) {
        return save(bookDto.id(), bookDto.title(), bookDto.author().id(),
                bookDto.genres().stream().map(GenreDto::id).collect(Collectors.toSet()));
    }

    @Override
    public BookDto toDto(Book book) {
        return bookMapper.toDto(book);
    }

    @Override
    public Book toObject(BookDto bookDto) {
        return bookMapper.toObject(bookDto);
    }
}

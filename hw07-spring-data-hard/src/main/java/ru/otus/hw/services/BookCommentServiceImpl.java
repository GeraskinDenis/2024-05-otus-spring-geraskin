package ru.otus.hw.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.Mapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.repositories.BookCommentsRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookCommentServiceImpl implements BookCommentService {

    private final BookCommentsRepository bookCommentsRepository;

    private final Mapper<BookComment, BookCommentDto> bookCommentMapper;

    private final BookService bookService;

    public BookCommentServiceImpl(BookCommentsRepository bookCommentsRepository,
                                  @Qualifier("bookCommentMapper") Mapper<BookComment, BookCommentDto> bookCommentMapper,
                                  BookService bookService) {
        this.bookCommentsRepository = bookCommentsRepository;
        this.bookCommentMapper = bookCommentMapper;
        this.bookService = bookService;
    }

    @Override
    public Integer countByBookId(Long id) {
        return bookCommentsRepository.countByBookId(id);
    }

    @Transactional
    @Override
    public void deleteAllByBookId(long bookId) {
        bookCommentsRepository.deleteByBookId(bookId);
    }

    @Transactional
    @Override
    public void deleteById(long bookCommentId) {
        bookCommentsRepository.deleteById(bookCommentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookCommentDto> findByBookId(long bookId) {
        return bookCommentsRepository.findByBookId(bookId)
                .stream()
                .map(bookCommentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BookCommentDto> findById(long id) {
        Optional<BookComment> bookComment = bookCommentsRepository.findById(id);
        if (bookComment.isPresent()) {
            return bookComment.map(bookCommentMapper::toDto);
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public BookComment findByIdOrThrow(long id) {
        return bookCommentsRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Book comment not found by ID: " + id));
    }

    @Transactional
    @Override
    public BookCommentDto save(long id, long bookId, String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            throw new IllegalArgumentException("The comment text must not be empty.");
        }
        Book book = bookService.findByIdOrThrow(bookId);
        BookComment bookComment = bookCommentsRepository.save(new BookComment(id, book, text));
        return bookCommentMapper.toDto(bookComment);
    }

    public BookCommentDto save(BookCommentDto bookCommentDto) {
        return save(bookCommentDto.id(), bookCommentDto.book().id(), bookCommentDto.text());
    }
}

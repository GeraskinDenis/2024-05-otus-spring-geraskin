package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.BookCommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.repositories.BookCommentsRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookCommentServiceImpl implements BookCommentService {

    private final BookCommentsRepository bookCommentsRepository;

    private final BookCommentMapper bookCommentMapper;

    private final BookRepository bookRepository;

    @Override
    public Integer getNumberByBookId(String id) {
        return bookCommentsRepository.countByBookId(id);
    }

    @Transactional
    @Override
    public void deleteAllByBookId(String bookId) {
        bookCommentsRepository.deleteByBookId(bookId);
    }

    @Transactional
    @Override
    public void deleteById(String bookCommentId) {
        bookCommentsRepository.deleteById(bookCommentId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BookCommentDto> findById(String id) {
        Optional<BookComment> bookComment = bookCommentsRepository.findById(id);
        if (bookComment.isPresent()) {
            return bookComment.map(bookCommentMapper::toDto);
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookCommentDto> findByBook(String bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        return optionalBook.map(book -> bookCommentsRepository.findByBook(book)
                .stream()
                .map(bookCommentMapper::toDto)
                .toList()).orElseGet(ArrayList::new);
    }

    @Transactional
    @Override
    public BookCommentDto insert(String bookId, String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            throw new IllegalArgumentException("The comment text must not be empty.");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found by ID:" + bookId));
        BookComment bookComment = bookCommentsRepository.save(new BookComment(null, book, text));
        return bookCommentMapper.toDto(bookComment);
    }

    @Transactional
    @Override
    public BookCommentDto update(String id, String bookId, String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            throw new IllegalArgumentException("The comment text must not be empty.");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found by ID:" + bookId));
        BookComment bookComment = bookCommentsRepository.save(new BookComment(id, book, text));
        return bookCommentMapper.toDto(bookComment);
    }

    private Book getExistingBookById(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found by ID: " + bookId));
    }
}

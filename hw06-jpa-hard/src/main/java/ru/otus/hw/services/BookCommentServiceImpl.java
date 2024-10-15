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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookCommentServiceImpl implements BookCommentService {

    private final BookCommentsRepository bookCommentsRepository;

    private final BookCommentMapper bookCommentMapper;

    private final BookRepository bookRepository;

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
    public List<BookCommentDto> findAllByBookId(long bookId) {
        return bookCommentsRepository.findAllByBookId(bookId)
                .stream()
                .map(bookCommentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public BookCommentDto insert(long bookId, String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            throw new IllegalArgumentException("The comment text must not be empty.");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found by ID:" + bookId));
        BookComment bookComment = bookCommentsRepository.save(new BookComment(0, book, text));
        return bookCommentMapper.toDto(bookComment);
    }

    @Transactional
    @Override
    public BookCommentDto update(long id, long bookId, String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            throw new IllegalArgumentException("The comment text must not be empty.");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found by ID:" + bookId));
        BookComment bookComment = bookCommentsRepository.save(new BookComment(id, book, text));
        return bookCommentMapper.toDto(bookComment);
    }

    @Transactional
    @Override
    public void deleteById(long bookCommentId) {
        bookCommentsRepository.deleteById(bookCommentId);
    }

    @Transactional
    @Override
    public void deleteAllByBookId(long bookId) {
        bookCommentsRepository.deleteAllByBookId(bookId);
    }
}

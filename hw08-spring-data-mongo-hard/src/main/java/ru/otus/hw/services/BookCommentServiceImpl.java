package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.BookCommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.repositories.BookCommentsRepository;
import ru.otus.hw.utils.CommonUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookCommentServiceImpl implements BookCommentService {

    private final BookCommentsRepository bookCommentsRepository;

    private final BookService bookService;

    private final BookCommentMapper bookCommentMapper;

    @Override
    public int countByBookId(String bookId) {
        return bookCommentsRepository.countByBookId(bookId);
    }

    @Override
    public void deleteAllByBookId(String bookId) {
        bookCommentsRepository.deleteByBookId(bookId);
    }

    @Override
    public void deleteById(String id) {
        bookCommentsRepository.deleteById(id);
    }

    @Override
    public List<BookComment> findByBookId(String bookId) {
        return bookCommentsRepository.findByBookId(bookId);
    }

    @Override
    public Optional<BookComment> findById(String id) {
        return bookCommentsRepository.findById(id);
    }

    @Override
    public BookComment findByIdOrThrow(String id) {
        return bookCommentsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("'BookComment' not found by id: " + id));
    }

    @Override
    public BookComment insert(String bookId, String text) {
        Book book = bookService.findByIdOrThrow(bookId);
        BookComment bookComment = new BookComment(CommonUtils.getUUID(), book, text);
        return bookCommentsRepository.save(bookComment);
    }

    @Override
    public BookCommentDto toDto(BookComment bookComment) {
        return bookCommentMapper.toDto(bookComment);
    }

    @Override
    public BookComment update(String id, String bookId, String text) {
        Book book = bookService.findByIdOrThrow(bookId);
        BookComment bookComment = findByIdOrThrow(id);
        bookComment.setBook(book);
        bookComment.setText(text);
        return bookCommentsRepository.save(bookComment);
    }
}

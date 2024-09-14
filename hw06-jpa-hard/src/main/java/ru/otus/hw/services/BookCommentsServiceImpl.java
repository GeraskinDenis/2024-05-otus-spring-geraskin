package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.mappers.BookCommentMapper;
import ru.otus.hw.repositories.BookCommentsRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookCommentsServiceImpl implements BookCommentsService {

    private final BookCommentsRepository bookCommentsRepository;

    private final BookCommentMapper bookCommentMapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<BookCommentDto> findById(long id) {
        return bookCommentsRepository.findById(id)
                .map(bookCommentMapper::toDto);
    }
}

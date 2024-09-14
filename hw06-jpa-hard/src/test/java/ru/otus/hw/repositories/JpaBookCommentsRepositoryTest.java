package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.converters.BookCommentConverter;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.mappers.BookCommentMapper;
import ru.otus.hw.mappers.BookCommentMapperImpl;

@DisplayName("Репозиторий на основе Jpa со студентами")
@DataJpaTest
@Import({JpaBookCommentsRepository.class, BookCommentMapperImpl.class, BookCommentConverter.class})
public class JpaBookCommentsRepositoryTest {

    @Autowired
    private JpaBookCommentsRepository repository;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookCommentMapper mapper;

    @Autowired
    private BookCommentConverter converter;

    @Test
    void findById(){
        BookCommentDto dto = repository.findById(4).map(mapper::toDto).get();
        System.out.println(converter.bookCommentToString(dto));
    }

}

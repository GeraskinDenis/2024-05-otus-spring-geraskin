package ru.otus.hw.dao;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CsvQuestionDao.class)
public class CsvQuestionDaoTest {

    @MockBean
    private TestFileNameProvider testFileNameProvider;

    @Autowired
    private CsvQuestionDao questionDao;

    @BeforeEach
    public void beforeEach() {
        when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");
    }

    @Test
    @DisplayName("Should have find all questions correctly")
    public void shouldFindAllCorrectlyEn() {
        List<Answer> answers = Arrays.asList(new Answer("Answer1", false),
                new Answer("Answer2", false),
                new Answer("Answer3", true));
        Question expectedQuestion = new Question("Question text?", answers);

        List<Question> questionList = questionDao.findAll();
        Question dummyQuestion = new Question("Dummy question", Arrays.asList(new Answer("Dummy answer", true)));
        assertThat(questionList).isNotNull()
                .isNotEmpty().contains(expectedQuestion)
                .hasSize(1).doesNotContain(dummyQuestion);
    }
}

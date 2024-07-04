package ru.otus.hw.dao;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

	private CsvQuestionDao csvQuestionDao;

	@BeforeEach
	public void beforeEach() {
		TestFileNameProvider testFileNameProvider = Mockito.mock(AppProperties.class);
		Mockito.when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");
		csvQuestionDao = new CsvQuestionDao(testFileNameProvider);
	}

	@Test
	@DisplayName("Should have find all questions correctly")
	public void shouldFindAllCorrectly() {
		List<Answer> answers = Arrays.asList(new Answer("Answer1", false),
				new Answer("Answer2", false),
				new Answer("Answer3", true));
		Question expectedAnswer = new Question("Question text?", answers);
		List<Question> questionList = csvQuestionDao.findAll();
		assertThat(questionList).isNotNull().isNotEmpty().contains(expectedAnswer).hasSize(1);
	}
}

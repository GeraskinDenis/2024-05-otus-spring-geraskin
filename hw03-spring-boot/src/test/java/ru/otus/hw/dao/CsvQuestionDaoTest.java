package ru.otus.hw.dao;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

	@Mock
	private TestFileNameProvider testFileNameProvider;

	@InjectMocks
	private CsvQuestionDao questionDao;

	@BeforeEach
	public void beforeEach() {
		Mockito.when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");
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

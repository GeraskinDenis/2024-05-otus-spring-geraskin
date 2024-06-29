package ru.otus.hw.dao;

import lombok.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

	private static Properties properties;

	private static TestFileNameProvider testFileNameProvider;

	private CsvQuestionDao csvQuestionDao;

	@BeforeAll
	static void beforeAll() {
		InputStream inputStream = CsvQuestionDaoTest.class.getClassLoader()
				.getResourceAsStream("application.properties");
		try (inputStream) {
			properties = new Properties();
			properties.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		testFileNameProvider = new AppProperties(
				Integer.parseInt(properties.getProperty("test.rightAnswersCountToPass")),
				properties.getProperty("test.fileName"));
	}

	@BeforeEach
	public void beforeEach() {
		csvQuestionDao = new CsvQuestionDao(testFileNameProvider);
	}

	@Test
	public void shouldFindAllCorrectly() {
		List<Answer> answers = Arrays.asList(new Answer("Answer1", false),
				new Answer("Answer2", false),
				new Answer("Answer3", true));
		Question expectedAnswer = new Question("Question text?", answers);
		List<Question> questionList = csvQuestionDao.findAll();
		assertThat(questionList).contains(expectedAnswer);
	}
}

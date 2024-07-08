package ru.otus.hw.dao;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.LocaleConfig;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CsvQuestionDaoTest {

	@Autowired
	private LocaleConfig localeConfig;

	@Autowired
	private TestFileNameProvider testFileNameProvider;

	@Autowired
	private CsvQuestionDao csvQuestionDao;

	@Test
	@DisplayName("Should have find all questions correctly")
	public void shouldFindAllCorrectlyEn() {
		Question expectedQuestion;
		if (localeConfig.getLocale().equals(Locale.forLanguageTag("en-US"))) {
			List<Answer> answers = Arrays.asList(new Answer("Answer1", false),
					new Answer("Answer2", false),
					new Answer("Answer3", true));
			expectedQuestion = new Question("Question text?", answers);
		} else if (localeConfig.getLocale().equals(Locale.forLanguageTag("ru-RU"))) {
			List<Answer> answers = Arrays.asList(new Answer("Ответ1", false),
					new Answer("Ответ2", false),
					new Answer("Ответ3", true));
			expectedQuestion = new Question("Это текст тестового вопрос?", answers);
		} else {
			expectedQuestion = null;
		}
		List<Question> questionList = csvQuestionDao.findAll();
		Question dummyQuestion = new Question("Dummy question", Arrays.asList(new Answer("Dummy answer", true)));
		assertThat(questionList).isNotNull()
				.isNotEmpty().contains(expectedQuestion)
				.hasSize(1).doesNotContain(dummyQuestion);
	}
}

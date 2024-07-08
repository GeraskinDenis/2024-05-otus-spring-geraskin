package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.LocalizedMessagesService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CsvQuestionDao implements QuestionDao {

	private final LocalizedMessagesService messagesService;

	private final TestFileNameProvider fileNameProvider;

	public CsvQuestionDao(@Qualifier("LocalizedMessagesServiceImpl") LocalizedMessagesService messagesService,
						  TestFileNameProvider fileNameProvider) {
		this.messagesService = messagesService;
		this.fileNameProvider = fileNameProvider;
	}

	@Override
	public List<Question> findAll() {
		List<Question> questions;
		InputStream inputStream = getFileAsStream(fileNameProvider.getTestFileName());
		try (InputStreamReader streamReader = new InputStreamReader(inputStream);
			 BufferedReader reader = new BufferedReader(streamReader)) {
			questions = new CsvToBeanBuilder<QuestionDto>(reader)
					.withType(QuestionDto.class)
					.withSkipLines(1)
					.withSeparator(';')
					.build()
					.parse()
					.stream()
					.map(QuestionDto::toDomainObject)
					.collect(Collectors.toList());
		} catch (IOException | RuntimeException e) {
			throw new QuestionReadException(messagesService.getMessage(
					"CsvQuestionDao.exception.error.reading.questions"), e);
		}
		if (questions.isEmpty()) {
			throw new QuestionReadException(messagesService.getMessage(
					"CsvQuestionDao.exception.questions.not.found"));
		}

		return questions;
	}

	private InputStream getFileAsStream(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);

		if (Objects.isNull(inputStream)) {
			throw new QuestionReadException(messagesService.getMessage(
					"CsvQuestionDao.exception.questions.source.not.found"));
		}

		return inputStream;
	}
}

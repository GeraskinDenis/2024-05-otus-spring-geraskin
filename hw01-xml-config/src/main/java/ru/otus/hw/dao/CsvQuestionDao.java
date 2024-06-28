package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
	private final TestFileNameProvider fileNameProvider;

	@Override
	public List<Question> findAll() {
		// Использовать CsvToBean
		// https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
		// Использовать QuestionReadException
		// Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/
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
		} catch (IOException e) {
			throw new QuestionReadException("Read error");
		}

		return questions;
	}

	private InputStream getFileAsStream(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);

		if (Objects.isNull(inputStream)) {
			throw new QuestionReadException("File not found: " + fileName);
		}

		return inputStream;
	}
}

package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class AppProperties implements TestConfig, TestFileNameProvider {

	// внедрить свойство из application.properties
	private final int rightAnswersCountToPass;

	// внедрить свойство из application.properties
	private final String testFileName;

	public AppProperties(@Value("${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
						 @Value("${test.fileName}") String testFileName) {
		this.rightAnswersCountToPass = rightAnswersCountToPass;
		this.testFileName = testFileName;
	}

	@Override
	public int getRightAnswersCountToPass() {
		return rightAnswersCountToPass;
	}

	@Override
	public String getTestFileName() {
		return testFileName;
	}
}

package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

	private final IOService ioService;

	private final QuestionDao questionDao;

	@Override
	public void executeTest() {
		ioService.printLine("");
		ioService.printFormattedLine("Please answer the questions below%n");
		// Получить вопросы из дао и вывести их с вариантами ответов
		questionDao.findAll().forEach(e -> {
			ioService.printFormattedLine("Question: %s", e.text());
			e.answers().forEach(a -> ioService.printFormattedLine("\t%s", a.text()));
		});
	}
}

package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

	private final IOService ioService;

	private final QuestionDao questionDao;

	@Override
	public void executeTest() {
		ioService.printLine("");
		ioService.printFormattedLine("Please answer the questions below%n");
		// Получить вопросы из дао и вывести их с вариантами ответов
		var questions = questionDao.findAll();
		for (Question question : questions) {
			showQuestion(question);
			showAnswers(question.answers());
		}
	}

	private void showQuestion(Question question) {
		ioService.printLine(question.text());
	}

	private void showAnswers(List<Answer> answers) {
		answers.forEach((a) -> ioService.printFormattedLine("\t%s", a.text()));
	}
}

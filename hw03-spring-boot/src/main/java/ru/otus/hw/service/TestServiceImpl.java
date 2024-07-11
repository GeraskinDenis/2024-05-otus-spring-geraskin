package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        for (var question : questions) {
            testResult.applyAnswer(question, getAnswer(question).isCorrect());
        }
        return testResult;
    }

    private Answer getAnswer(Question question) {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        Map<Integer, Answer> answers = question.answers().stream()
                .collect(Collectors.toMap(e -> atomicInteger.getAndIncrement(), Function.identity()));
        showQuestion(question);
        showAnswers(answers);
        return getAnswer(answers);
    }

    private void showQuestion(Question question) {
        ioService.printLine(question.text());
    }

    private void showAnswers(Map<Integer, Answer> answers) {
        answers.forEach((key, value) -> ioService.printFormattedLine("%s. %s", key, value.text()));
    }

    private Answer getAnswer(Map<Integer, Answer> answers) {
        int answerNumber = ioService.readIntForRangeWithPromptLocalized(1,
                answers.size(),
                "TestService.enter.answer.number",
                "TestService.incorrect.answer",
                "TestService.interrupted.exceeded.number");
        return answers.get(answerNumber);
    }

}

package ru.otus.hw.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.TestResult;

@Service
public class ResultServiceImpl implements ResultService {

    private final TestConfig testConfig;

    private final LocalizedMessagesService messagesService;

    public ResultServiceImpl(TestConfig testConfig,
                             @Qualifier("localizedMessagesServiceImpl")
                             LocalizedMessagesService messagesService) {
        this.testConfig = testConfig;
        this.messagesService = messagesService;
    }

    @Override
    public String getTestResultReport(TestResult testResult) {
        StringBuilder builder = new StringBuilder("\n")
                .append(" ")
                .append(messagesService.getMessage("ResultService.test.results"))
                .append(" ")
                .append(messagesService.getMessage("ResultService.student",
                        testResult.getStudent().getFullName()))
                .append(" ")
                .append(messagesService.getMessage("ResultService.answered.questions.count",
                        testResult.getAnsweredQuestions().size()))
                .append(" ")
                .append(messagesService.getMessage("ResultService.right.answers.count",
                        testResult.getRightAnswersCount()))
                .append(" ");
        if (testResult.getRightAnswersCount() >= testConfig.getRightAnswersCountToPass()) {
            builder.append(messagesService.getMessage("ResultService.passed.test"));
        } else {
            builder.append(messagesService.getMessage("ResultService.fail.test"));
        }
        return builder.toString();
    }
}


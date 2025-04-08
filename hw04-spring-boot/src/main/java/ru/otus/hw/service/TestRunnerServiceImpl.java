package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    @Qualifier("ioService")
    private final IOService ioService;

    public TestRunnerServiceImpl(TestService testService, StudentService studentService,
                                 ResultService resultService, @Qualifier("ioService") IOService ioService) {
        this.testService = testService;
        this.studentService = studentService;
        this.resultService = resultService;
        this.ioService = ioService;
    }

    @Override
    public void run() {
        try {
            var student = studentService.determineCurrentStudent();
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (IllegalArgumentException ex) {
            ioService.printLine(ex.getMessage());
        }
    }
}

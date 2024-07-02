package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

	private final TestService testService;

	private final StudentService studentService;

	private final ResultService resultService;

	private final IOService ioService;

	@Override
	public void run() {
		try {
			var student = studentService.determineCurrentStudent();
			var testResult = testService.executeTestFor(student);
			resultService.showResult(testResult);
		} catch (RuntimeException e) {
			ioService.printLine(e.getMessage());
		}
	}
}
package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

	private final TestService testService;

	private final IOService ioService;

	@Override
	public void run() {
		try {
			testService.executeTest();
		} catch (Exception e) {
			ioService.printLine("Errors occurred while the application was running:");
			ioService.printLine(e.getMessage());
		}
	}
}

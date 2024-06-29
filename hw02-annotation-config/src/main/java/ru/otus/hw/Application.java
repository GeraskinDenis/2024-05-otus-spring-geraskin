package ru.otus.hw;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.hw.service.TestRunnerService;

public class Application {
	public static void main(String[] args) {

		//Создать контекст на основе Annotation/Java конфигурирования
		ApplicationContext context = new AnnotationConfigApplicationContext("ru.otus.hw.config");
		var testRunnerService = context.getBean(TestRunnerService.class);
		testRunnerService.run();
	}
}
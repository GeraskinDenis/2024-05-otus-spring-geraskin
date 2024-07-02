package ru.otus.hw;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.hw.service.TestRunnerService;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        //Создать контекст Spring Boot приложения
        ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();

    }
}
package ru.otus.hw.shell;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.LocalizedMessagesService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestService;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@ShellComponent(value = "Test commands")
public class ApplicationEventsCommands {

    private final LocalizedMessagesService messagesService;

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private Student student;

    private TestResult testResult;

    public ApplicationEventsCommands(@Qualifier("localizedMessagesServiceImpl") LocalizedMessagesService messagesService,
                                     TestService testService,
                                     StudentService studentService,
                                     ResultService resultService) {
        this.messagesService = messagesService;
        this.testService = testService;
        this.studentService = studentService;
        this.resultService = resultService;
    }

    @ShellMethod(value = "Login", key = {"l", "login"})
    @ShellMethodAvailability("isNotUserLoggedIn")
    public String login(@ShellOption(help = "First name") String firstName,
                        @ShellOption(help = "Last name") String lastName) {

        student = studentService.determineCurrentStudent(firstName, lastName);
        return String.format(messagesService.getMessage("ApplicationEventsCommands.welcome.student")
                , student.getFullName());
    }

    @ShellMethod(value = "Logout", key = {"out", "logout"})
    @ShellMethodAvailability("isUserLoggedIn")
    public String logout() {
        student = null;
        return String.format(messagesService.getMessage("ApplicationEventsCommands.see.you.soon"));
    }

    @ShellMethod(value = "Start test", key = {"test", "t"})
    @ShellMethodAvailability("isUserLoggedIn")
    public String test() {
        try {
            testResult = testService.executeTestFor(student);
            return result();
        } catch (Exception e) {
            return messagesService.getMessage("TestRunnerService.exception.message.error")
                    + e.getMessage();
        }
    }

    @ShellMethod(value = "Show test result", key = {"result", "r"})
    @ShellMethodAvailability("hasTestResult")
    public String result() {
        return resultService.getTestResultReport(testResult);
    }

    public Availability isUserLoggedIn() {
        return nonNull(student)
                ? Availability.available()
                : Availability.unavailable(messagesService.getMessage("ApplicationEventsCommands.you.are.not.logged"));
    }

    public Availability isNotUserLoggedIn() {
        return isNull(student)
                ? Availability.available()
                : Availability.unavailable(messagesService.getMessage("ApplicationEventsCommands.already.logged.in"));
    }

    public Availability hasTestResult() {
        return nonNull(testResult)
                ? Availability.available()
                : Availability.unavailable(messagesService.getMessage("ApplicationEventsCommands.test.result.missing"));
    }
}

package ru.otus.hw.shell;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.domain.TestSession;
import ru.otus.hw.service.LocalizedMessagesService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestService;

import java.util.Objects;

@ShellComponent(value = "Application commands")
public class ApplicationEventsCommands {

    private final LocalizedMessagesService messagesService;

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private TestSession testSession;

    public ApplicationEventsCommands(@Qualifier("localizedMessagesService") LocalizedMessagesService messagesService,
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

        Student student = studentService.determineCurrentStudent(firstName, lastName);
        testSession = new TestSession(student);
        return String.format(messagesService.getMessage("ApplicationEventsCommands.welcome.student")
                , student.getFullName());
    }

    @ShellMethod(value = "Logout", key = {"out", "logout"})
    @ShellMethodAvailability("isUserLoggedIn")
    public String logout() {
        testSession = null;
        return String.format(messagesService.getMessage("ApplicationEventsCommands.see.you.soon"));
    }

    @ShellMethod(value = "Start test", key = {"test", "t"})
    @ShellMethodAvailability("isUserLoggedIn")
    public String test() {
        try {
            TestResult testResult = testService.executeTestFor(testSession.getStudent());
            testSession.setTestResult(testResult);
            return result();
        } catch (Exception e) {
            return messagesService.getMessage("TestRunnerService.exception.message.error")
                    + e.getMessage();
        }
    }

    @ShellMethod(value = "Show test result", key = {"result", "r"})
    @ShellMethodAvailability("hasTestResult")
    public String result() {
        return resultService.getTestResultReport(testSession.getTestResult());
    }

    public Availability isUserLoggedIn() {
        return Objects.nonNull(testSession)
                ? Availability.available()
                : Availability.unavailable(messagesService.getMessage("ApplicationEventsCommands.you.are.not.logged"));
    }

    public Availability isNotUserLoggedIn() {
        return Objects.isNull(testSession)
                ? Availability.available()
                : Availability.unavailable(messagesService.getMessage("ApplicationEventsCommands.already.logged.in"));
    }

    public Availability hasTestResult() {
        return Objects.nonNull(testSession)
                ? Availability.available()
                : Availability.unavailable(messagesService.getMessage("ApplicationEventsCommands.test.result.missing"));
    }
}

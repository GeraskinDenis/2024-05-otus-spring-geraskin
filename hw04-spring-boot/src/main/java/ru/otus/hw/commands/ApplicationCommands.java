package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw.service.TestRunnerService;

@Command(group = "Application commands")
@RequiredArgsConstructor
public class ApplicationCommands {

    private final TestRunnerService testRunnerService;

    @Command(command = "start", description = "start test")
    public void start() {
        testRunnerService.run();
    }
}

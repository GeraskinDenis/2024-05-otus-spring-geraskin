package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.services.AuthorService;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class TestCommands {



    Logger log = Logger.getLogger(TestCommands.class.getName());

    @ShellMethod(value = "Print one parameter", key = "t1")
    public void t1(@ShellOption(value = "s", help = "First parameter (String)") String str) {
        log.info(str);
    }

    @ShellMethod(value = "Print two parameters", key = "t2")
    public void t2(@ShellOption(value = "p1", help = "Parameter (String)") String p1,
                   @ShellOption(value = "p2", help = "Parameter (Integer)") Long p2) {
        log.info(p1);
        log.info(p2.toString());
    }

    @ShellMethod(value = "Print three parameters", key = "t3")
    public void t3(@ShellOption(value = "p1", help = "Parameter (String)") String p1,
                   @ShellOption(value = "p2", help = "Parameter (Integer)") Long p2,
                   @ShellOption(value = "p3", help = "Parameter (List<Long>)") Set<Long> p3) {
        log.info(p1);
        log.info(p2.toString());
        log.info("List<Long>: Size: " + p3.size() + " Values: " + p3);
    }
}

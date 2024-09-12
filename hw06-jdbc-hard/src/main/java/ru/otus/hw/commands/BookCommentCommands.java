package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.converters.BookCommentConverter;
import ru.otus.hw.services.BookCommentsService;

@ShellComponent
@RequiredArgsConstructor
public class BookCommentCommands {
    private final BookCommentsService bookCommentsService;

    private final BookCommentConverter bookCommentConverter;

    @ShellMethod(value = "Find a book comment by ID", key = "cf")
    public String findById(@ShellOption(value = "id", help = "book comment ID") long id) {
        return bookCommentsService.findById(id)
                .map(bookCommentConverter::bookCommentToString)
                .orElse("No comment found for book by ID: " + id);
    }

}

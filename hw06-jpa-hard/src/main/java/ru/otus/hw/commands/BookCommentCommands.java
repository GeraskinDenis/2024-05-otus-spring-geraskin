package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.converters.BookCommentConverter;
import ru.otus.hw.services.BookCommentService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class BookCommentCommands {
    private final BookCommentService bookCommentService;

    private final BookCommentConverter bookCommentConverter;

    @ShellMethod(value = "Find a book comment by ID", key = "cf")
    public String findById(@ShellOption(value = "id", help = "book comment ID") long id) {
        return bookCommentService.findById(id)
                .map(bookCommentConverter::bookCommentToString)
                .orElse("No comment found for book by ID: " + id);
    }

    @ShellMethod(value = "Find all comments by book ID", key = "ca")
    public String findAllByBookId(@ShellOption(value = "bookId", help = "book id") long bookId) {
        return bookCommentService
                .findAllByBookId(bookId)
                .stream().map(bookCommentConverter::bookCommentToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Insert a new book comment", key = "ci")
    public String insert(@ShellOption(value = "bookId", help = "Book ID") long bookId,
                         @ShellOption(value = "textComment", help = "text of comment") String textComment) {
        return bookCommentConverter.bookCommentToString(bookCommentService.insert(bookId, textComment));
    }

    @ShellMethod(value = "Update a book comment", key = "cu")
    public String insert(@ShellOption(value = "id", help = "book comment ID") long id,
                         @ShellOption(value = "bookId", help = "Book ID") long bookId,
                         @ShellOption(value = "textComment", help = "text of comment") String textComment) {
        return bookCommentConverter.bookCommentToString(bookCommentService.update(id, bookId, textComment));
    }

    @ShellMethod(value = "Delete a book comment by ID", key = "cd")
    public String deleteById(@ShellOption(value = "id", help = "book comment ID") long id) {
        bookCommentService.deleteById(id);
        return "Deletion a Book comment by ID (%s) is completed.".formatted(id);
    }

    @ShellMethod(value = "Delete all book comments by book ID", key = "cda")
    public String deleteAllByBookId(@ShellOption(value = "bookId", help = "book ID") long bookId) {
        bookCommentService.deleteAllByBookId(bookId);
        return "Deleting all Book comments by book ID (%s) is completed.".formatted(bookId);
    }
}

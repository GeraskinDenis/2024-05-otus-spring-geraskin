package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.converters.BookCommentConverter;
import ru.otus.hw.services.BookCommentsService;

import java.util.stream.Collectors;

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

    @ShellMethod(value = "Find all comments by book ID", key = "ca")
    public String findAllByBookId(@ShellOption(value = "bookId", help = "book id") long bookId) {
        return bookCommentsService
                .findAllByBookId(bookId)
                .stream().map(bookCommentConverter::bookCommentToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Insert a new book comment", key = "ci")
    public String insert(@ShellOption(value = "bookId", help = "Book ID") long bookId,
                         @ShellOption(value = "textComment", help = "text of comment") String textComment) {
        return bookCommentConverter.bookCommentToString(bookCommentsService.insert(bookId, textComment));
    }

    @ShellMethod(value = "Update a book comment", key = "cu")
    public String insert(@ShellOption(value = "id", help = "book comment ID") long id,
                         @ShellOption(value = "bookId", help = "Book ID") long bookId,
                         @ShellOption(value = "textComment", help = "text of comment") String textComment) {
        return bookCommentConverter.bookCommentToString(bookCommentsService.update(id, bookId, textComment));
    }

    @ShellMethod(value = "Delete a book comment by ID", key = "cd")
    public String deleteById(@ShellOption(value = "id", help = "book comment ID") long id) {
        bookCommentsService.deleteById(id);
        return "Deleting a book comment by ID is complete!";
    }

    @ShellMethod(value = "Delete all book comments by book ID", key = "cda")
    public String deleteAllByBookId(@ShellOption(value = "bookId", help = "book ID") long bookId) {
        bookCommentsService.deleteAllByBookId(bookId);
        return "Deleting all book comments by book ID is complete!";
    }
}

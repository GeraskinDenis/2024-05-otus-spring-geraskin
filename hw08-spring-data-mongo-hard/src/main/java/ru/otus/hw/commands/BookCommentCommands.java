package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.converters.BookCommentConverter;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.services.BookCommentService;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class BookCommentCommands {
    private final BookCommentService bookCommentService;

    private final BookCommentConverter bookCommentConverter;

    @ShellMethod(value = "Delete all book comments by book ID", key = "cda")
    public String deleteAllByBookId(@ShellOption(value = "bookId", help = "book ID") String bookId) {
        bookCommentService.deleteAllByBookId(bookId);
        return "Deleting all Book comments by book ID (%s) is completed.".formatted(bookId);
    }

    @ShellMethod(value = "Delete a book comment by ID", key = "cd")
    public String deleteById(@ShellOption(value = "id", help = "book comment ID") String id) {
        bookCommentService.deleteById(id);
        return "Deletion a Book comment by ID (%s) is completed.".formatted(id);
    }

    @ShellMethod(value = "Find a book comment by ID", key = "cf")
    public String findById(@ShellOption(value = "id", help = "book comment ID") String id) {
        return bookCommentService.findById(id)
                .map(bookCommentConverter::bookCommentToString)
                .orElse("No book comment found by ID: " + id);
    }

    @ShellMethod(value = "Find all comments by book ID", key = "ca")
    public String findAllByBookId(@ShellOption(value = "bookId", help = "book id") String bookId) {
        List<BookCommentDto> list = bookCommentService.findByBook(bookId);
        if (list.isEmpty()) {
            return "No book comments found by book ID: " + bookId;
        } else {
            return list.stream().map(bookCommentConverter::bookCommentToString)
                    .collect(Collectors.joining(System.lineSeparator()));
        }
    }

    @ShellMethod(value = "Get the number of book comments by book Id", key = "cn")
    public String getNumberByBookId(@ShellOption(value = "bookId", help = "BookID") String bookId) {
        return bookCommentService.getNumberByBookId(bookId).toString();
    }

    @ShellMethod(value = "Insert a new book comment", key = "ci")
    public String insert(@ShellOption(value = "bookId", help = "Book ID") String bookId,
                         @ShellOption(value = "textComment", help = "text of comment") String textComment) {
        return bookCommentConverter.bookCommentToString(bookCommentService.insert(bookId, textComment));
    }

    @ShellMethod(value = "Update a book comment", key = "cu")
    public String update(@ShellOption(value = "id", help = "book comment ID") String id,
                         @ShellOption(value = "bookId", help = "Book ID") String bookId,
                         @ShellOption(value = "textComment", help = "text of comment") String textComment) {
        return bookCommentConverter.bookCommentToString(bookCommentService.update(id, bookId, textComment));
    }
}

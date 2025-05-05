package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.BookCommentConverter;
import ru.otus.hw.dto.BookCommentDto;
import ru.otus.hw.services.BookCommentService;

import java.util.List;
import java.util.stream.Collectors;

@Command(command = "book-comment", group = "Book comments commands")
@RequiredArgsConstructor
public class BookCommentCommands {

    private final BookCommentService bookCommentService;

    private final BookCommentConverter bookCommentConverter;

    @Command(command = "del-all-by-book", description = "delete all Book comments by Book ID")
    public String deleteAllByBookId(@Option(longNames = "bookId", required = true,
            arityMin = 1, label = "Book ID to delete Book comments") long bookId) {
        bookCommentService.deleteAllByBookId(bookId);
        return "Deleting all Book comments by Book ID (%s) is completed.".formatted(bookId);
    }

    @Command(command = "del", description = "delete a Book comment by ID")
    public String deleteById(@Option(longNames = "id", required = true, arityMin = 1, label = "Book comment ID")
                             long id) {
        bookCommentService.deleteById(id);
        return "Deletion a Book comment by ID (%s) is completed.".formatted(id);
    }

    @Command(command = "find", description = "find a Book comment by ID")
    public String findById(@Option(longNames = "id", required = true, arityMin = 1, label = "Book comment ID")
                           long id) {
        return bookCommentService.findById(id)
                .map(bookCommentConverter::bookCommentToString)
                .orElse("No Book comment found by ID: " + id);
    }

    @Command(command = "find-all-by-book", description = "find all Book comments by book ID")
    public String findAllByBookId(@Option(longNames = "bookId", required = true, arityMin = 1, label = "Book id")
                                  long bookId) {
        List<BookCommentDto> list = bookCommentService.findByBookId(bookId);
        if (list.isEmpty()) {
            return "No Book comments found by Book ID: " + bookId;
        } else {
            return list.stream().map(bookCommentConverter::bookCommentToString)
                    .collect(Collectors.joining(System.lineSeparator()));
        }
    }

    @Command(command = "count-by-book", description = "number of Book comments by Book Id")
    public String getNumberByBookId(@Option(longNames = "bookId", required = true, arityMin = 1, label = "Book ID")
                                    long bookId) {
        return bookCommentService.countByBookId(bookId).toString();
    }

    @Command(command = "add", description = "add a new Book comment")
    public String insert(@Option(longNames = "bookId", required = true, arityMin = 1, label = "Book ID") long bookId,
                         @Option(longNames = "textComment", required = true, label = "text of comment")
                         String textComment) {
        return bookCommentConverter.bookCommentToString(bookCommentService.save(0L, bookId, textComment));
    }

    @Command(command = "update", description = "update a Book comment")
    public String update(@Option(longNames = "id", required = true, arityMin = 1, label = "Book comment ID") long id,
                         @Option(longNames = "bookId", required = true, arityMin = 1, label = "new Book ID") long bookId,
                         @Option(longNames = "textComment", required = true, arityMin = 1, label = "new text of comment")
                         String textComment) {
        return bookCommentConverter.bookCommentToString(bookCommentService.save(id, bookId, textComment));
    }
}

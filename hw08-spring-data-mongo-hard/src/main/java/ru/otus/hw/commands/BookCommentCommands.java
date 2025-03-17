package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.BookCommentConverter;
import ru.otus.hw.mappers.BookCommentMapper;
import ru.otus.hw.services.BookCommentService;

import java.util.stream.Collectors;

@Command(group = "Book commentary commands", command = "book-com")
@RequiredArgsConstructor
public class BookCommentCommands {

    private final BookCommentService bookCommentService;

    private final BookCommentMapper bookCommentMapper;

    private final BookCommentConverter bookCommentConverter;

    @Command(command = "del", description = "delete a book comment by ID")
    public String deleteById(
            @Option(longNames = "id", required = true, description = "book comment ID") String id) {
        bookCommentService.deleteById(id);
        return "Deleting a Book comment by ID (%s) is completed.".formatted(id);
    }

    @Command(command = "del-by-book", description = "delete all book comments by book ID")
    public String deleteAllByBookId(
            @Option(longNames = "bookId", required = true, description = "book ID") String bookId) {
        bookCommentService.deleteByBookId(bookId);
        return "Deleting all Book comments by book ID (%s) is completed.".formatted(bookId);
    }

    @Command(command = "find", description = "find a book comment by ID")
    public String findById(
            @Option(longNames = "id", required = true, description = "book comment ID") String id) {
        return bookCommentService.findById(id)
                .map(bookCommentMapper::toDto)
                .map(bookCommentConverter::bookCommentToString)
                .orElse("Book comment not found by ID: " + id);
    }

    @Command(command = "find-by-book", description = "find all book comments by book ID")
    public String findAllByBookId(
            @Option(longNames = "bookId", required = true, description = "book id") String bookId) {
        String bookCommentsStr = bookCommentService.findByBookId(bookId).stream()
                .map(bookCommentService::toDto)
                .map(bookCommentConverter::bookCommentToString)
                .collect(Collectors.joining(System.lineSeparator()));
        if (bookCommentsStr.isEmpty()) {
            return "Book comments not found by book ID: " + bookId;
        }
        return bookCommentsStr;
    }

    @Command(command = "count-by-book", description = "count book comments by book ID")
    public String countByBookId(@Option(longNames = "bookId", required = true, description = "book ID") String bookId) {
        return String.valueOf(bookCommentService.countByBookId(bookId));
    }

    @Command(command = "add", description = "add a new book comment")
    public String insert(@Option(longNames = "bookId", required = true, description = "book ID") String bookId,
                         @Option(longNames = "text", required = true, description = "text of comment")
                         String textComment) {
        return bookCommentConverter.bookCommentToString(
                bookCommentService.toDto(bookCommentService.insert(bookId, textComment)));
    }

    @Command(command = "update", description = "Update a book comment")
    public String update(@Option(longNames = "id", required = true, description = "book comment ID") String id,
                         @Option(longNames = "bookId", required = true, description = "book ID") String bookId,
                         @Option(longNames = "text", required = true, description = "new text of comment")
                         String textComment) {
        return bookCommentConverter.bookCommentToString(
                bookCommentService.toDto(bookCommentService.update(id, bookId, textComment)));
    }
}

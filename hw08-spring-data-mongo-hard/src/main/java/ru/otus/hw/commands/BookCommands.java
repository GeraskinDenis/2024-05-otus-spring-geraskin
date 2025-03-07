package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.services.BookService;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Command(group = "Book commands", command = "book")
@RequiredArgsConstructor
public class BookCommands {

    private final BookService bookService;

    private final BookConverter bookConverter;

    @Command(command = "del", description = "delete book by ID")
    public String delete(@Option(longNames = "id", required = true, description = "book id for delete") String id) {
        bookService.deleteById(id);
        return "Deletion Book by ID (%s) completed.".formatted(id);
    }

    @Command(command = "find", description = "find by ID (find all books)")
    public String findById(@Option(
            longNames = "id",
            description = "book ID (empty for all)") String id) {
        if (Objects.isNull(id)) {
            return bookService.findAll().stream()
                    .map(bookService::toDto)
                    .map(bookConverter::bookToString)
                    .collect(Collectors.joining(System.lineSeparator()));
        } else {
            return bookService.findById(id)
                    .map(bookService::toDto)
                    .map(bookConverter::bookToString)
                    .orElse("Book not found by ID (%s)".formatted(id));
        }
    }

    @Command(command = "find-by-title", description = "find books by title substring")
    public String findByTitle(
            @Option(longNames = "titleSubstring",
                    required = true,
                    description = "substring of book title for search") String titleSubstring) {
        return bookService.findByTitleSubstring(titleSubstring).stream()
                .map(bookService::toDto)
                .map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Command(command = "find-by-author", description = "find books by author full name substring")
    public String findByAuthor(
            @Option(longNames = "author",
                    required = true,
                    description = "author full name substring") String authorFullNameSubstring) {
        return bookService.findByAuthorFullNameSubstring(authorFullNameSubstring).stream()
                .map(bookService::toDto)
                .map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Command(command = "add", description = "add a new book")
    public String insert(
            @Option(longNames = "title", required = true, description = "title of new book") String title,
            @Option(longNames = "authorId", required = true, description = "author ID") String authorId,
            @Option(longNames = "genreIds", required = true, description = "list of genre ids (example: \"id1,id2,id3\")")
            Set<String> genreIds) {
        return bookConverter.bookToString(bookService.toDto(bookService.insert(title, authorId, genreIds)));
    }

    @Command(command = "update", description = "update book")
    public String update(
            @Option(longNames = "bookId", required = true, description = "book id") String id,
            @Option(longNames = "title", required = true, description = "title of new book") String title,
            @Option(longNames = "authorId", required = true, description = "author ID") String authorId,
            @Option(longNames = "genreIds", required = true, description = "list of genre ids (example: \"id1,id2,id3\")")
            Set<String> genreIds) {
        return bookConverter.bookToString(bookService.toDto(bookService.update(id, title, authorId, genreIds)));
    }
}

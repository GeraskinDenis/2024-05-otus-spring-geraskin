package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.services.BookService;

import java.util.Set;
import java.util.stream.Collectors;

@Command(command = "book", group = "Book commands")
@RequiredArgsConstructor
public class BookCommands {

    private final BookService bookService;

    private final BookConverter bookConverter;

    @Command(command = "del", description = "delete Book by id")
    public String delete(@Option(longNames = "id", required = true, arityMin = 1, label = "Book ID for delete")
                         long id) {
        bookService.deleteById(id);
        return "Deletion Book by ID (%s) completed.".formatted(id);
    }

    @Command(command = "find-all", description = "find all Books")
    public String findAll() {
        return bookService.findAll().stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Command(command = "find", description = "find Book by ID")
    public String findById(@Option(longNames = "id", required = true, label = "Book ID for search") long id) {
        return bookService.findById(id)
                .map(bookConverter::bookToString)
                .orElse("Book not found by ID (%s)".formatted(id));
    }

    @Command(command = "find-by-title", description = "find books by title")
    public String findByTitle(@Option(longNames = "titleSubstring", required = true, label = "substring of title")
                              String titleSubstring) {
        return bookService.findByTitleLike(titleSubstring).stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Command(command = "find-by-author", description = "find Books by Author name substring")
    public String findByAuthorByFullName(@Option(longNames = "authorFullNameSubstring", required = true,
            label = "Author full name substring")
                                         String authorFullNameSubstring) {
        return bookService.findByAuthorFullNameLike(authorFullNameSubstring)
                .stream().map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Command(command = "find-with-max-id", description = "find a Book with max ID")
    public String findWithMaxId() {
        return bookService.findWithMaxId()
                .map(bookConverter::bookToString)
                .orElse("No books found.");
    }

    @Command(command = "add", description = "add a new Book")
    public String insert(@Option(longNames = "title", required = true, label = "title of new Book") String title,
                         @Option(longNames = "authorId", required = true, label = "Author ID") long authorId,
                         @Option(longNames = "genereIds", required = true,
                                 label = "list of Genre ids", description = "Example: '1,2,3'")
                         Set<Long> genreIds) {
        return bookConverter.bookToString(bookService.save(0L, title, authorId, genreIds));
    }

    @Command(command = "update", description = "update Book")
    public String update(@Option(longNames = "id", required = true, arityMin = 1, label = "Book ID")
                         long id,
                         @Option(longNames = "title", required = true, label = "title of new Book")
                         String title,
                         @Option(longNames = "authorId", required = true, arityMin = 1, label = "Author ID")
                         long authorId,
                         @Option(longNames = "genereIds", required = true,
                                 label = "list of Genre ids", description = "Example: '1,2,3'")
                         Set<Long> genreIds) {
        var savedBook = bookService.save(id, title, authorId, genreIds);
        return bookConverter.bookToString(savedBook);
    }
}

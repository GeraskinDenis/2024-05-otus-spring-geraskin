package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.services.BookService;

import java.util.Set;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class BookCommands {

    private final BookService bookService;

    private final BookConverter bookConverter;

    @ShellMethod(value = "Find all books", key = "ba")
    public String findAll() {
        return bookService.findAll().stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id", key = "bf")
    public String findById(@ShellOption(value = "id", help = "book ID for search") long id) {
        return bookService.findById(id)
                .map(bookConverter::bookToString)
                .orElse("Book not found by ID (%s)".formatted(id));
    }

    @ShellMethod(value = "Find books by title", key = "book-find-by-title")
    public String findByTitle(@ShellOption(value = "titleSubstring",
            help = "substring of book title for search") String titleSubstring) {
        return bookService.findByTitleLike(titleSubstring).stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by author full name substring", key = "book-find-by-author")
    public String findByAuthorByFullName(@ShellOption(value = "authorFullNameSubstring",
            help = "author full name substring") String authorFullNameSubstring) {
        return bookService.findByAuthorFullNameLike(authorFullNameSubstring)
                .stream().map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Find a book with max id", key = "book-with-max-id")
    public String findWithMaxId() {
        return bookService.findWithMaxId()
                .map(bookConverter::bookToString)
                .orElse("No books found.");
    }

    @ShellMethod(value = "Insert a new book", key = "bi")
    public String insert(@ShellOption(value = "title", help = "title of new book") String title,
                         @ShellOption(value = "authorId", help = "author ID") long authorId,
                         @ShellOption(value = "genereIds", help = "list of genre ids (example: \"1,2,3\")")
                         Set<Long> genreIds) {
        return bookConverter.bookToString(bookService.insert(title, authorId, genreIds));
    }

    @ShellMethod(value = "Update book", key = "bu")
    public String update(@ShellOption(value = "id", help = "book id") long id,
                         @ShellOption(value = "title", help = "title of new book") String title,
                         @ShellOption(value = "authorId", help = "author ID") long authorId,
                         @ShellOption(value = "genereIds", help = "list of genre ids (example: \"1,2,3\")")
                         Set<Long> genreIds) {
        var savedBook = bookService.update(id, title, authorId, genreIds);
        return bookConverter.bookToString(savedBook);
    }

    @ShellMethod(value = "Delete book by id", key = "bd")
    public String delete(@ShellOption(value = "id", help = "book id for delete") long id) {
        bookService.deleteById(id);
        return "Deletion Book by ID (%s) completed.".formatted(id);
    }
}

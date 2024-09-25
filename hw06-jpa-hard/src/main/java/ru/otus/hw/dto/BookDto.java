package ru.otus.hw.dto;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;

import java.util.List;

public record BookDto(long id, String title, AuthorDto author, List<GenreDto> genres, List<BookCommentDto> bookComments) {
}

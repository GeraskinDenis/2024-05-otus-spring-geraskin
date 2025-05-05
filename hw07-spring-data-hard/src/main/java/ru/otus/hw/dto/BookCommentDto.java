package ru.otus.hw.dto;

public record BookCommentDto(long id, BookDto book, String text) {
}

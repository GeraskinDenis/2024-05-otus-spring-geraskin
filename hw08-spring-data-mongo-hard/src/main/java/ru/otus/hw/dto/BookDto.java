package ru.otus.hw.dto;

import java.util.List;

public record BookDto(String id, String uuid, String title, AuthorDto author, List<GenreDto> genres) {
}

package ru.otus.hw.repositories;

import java.util.List;
import java.util.Map;

public interface GenreReport {
    List<Map<String, Object>> getNumberOfBooksByGenre();
}

package ru.otus.hw.services;

import ru.otus.hw.dto.ReportDto;

import java.util.List;

public interface GenreReportService {

    ReportDto countBooksByGenre();

    ReportDto countBooksByGenre(List<Long> genreIds);
}

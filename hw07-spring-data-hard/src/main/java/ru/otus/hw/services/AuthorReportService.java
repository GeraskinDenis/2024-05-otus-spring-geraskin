package ru.otus.hw.services;

import ru.otus.hw.dto.ReportDto;

import java.util.List;

public interface AuthorReportService {

    ReportDto countBooksByAuthors();

    ReportDto countBooksByAuthors(List<Long> authorIds);
}

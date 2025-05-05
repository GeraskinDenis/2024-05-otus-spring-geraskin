package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.ReportDto;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.projections.NumberOfBooksByGenre;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreReportServiceImpl implements GenreReportService {

    private final GenreRepository genreRepository;

    @Override
    public ReportDto countBooksByGenre() {
        String reportName = "--- Number of books by genres ---";
        List<NumberOfBooksByGenre> queryResult = genreRepository.countBooksByGenres();
        return new ReportDto(reportName, convertToRows(queryResult));
    }

    @Override
    public ReportDto countBooksByGenre(List<Long> genreIds) {
        String reportName = "--- Number of books by genres ---";
        List<NumberOfBooksByGenre> queryResult = genreRepository.countBooksByGenres(genreIds);
        return new ReportDto(reportName, convertToRows(queryResult));
    }

    private List<List<String>> convertToRows(List<NumberOfBooksByGenre> rowDataList) {
        List<List<String>> rows = new ArrayList<>(rowDataList.size());
        rows.add(List.of("№", "ID", "Genre", "Number of books"));
        int i = 1;
        for (NumberOfBooksByGenre data : rowDataList) {
            rows.add(List.of(String.valueOf(i++), String.valueOf(data.getId()),
                    data.getName(), String.valueOf(data.getNumber())));
        }
        return rows;
    }
}

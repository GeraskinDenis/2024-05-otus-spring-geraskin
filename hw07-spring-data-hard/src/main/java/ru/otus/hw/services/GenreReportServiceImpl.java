package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.Report;
import ru.otus.hw.repositories.GenreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenreReportServiceImpl implements GenreReportService {
    private final GenreRepository genreRepository;

    @Override
    public Report getNumberOfBooksByGenre() {
        String reportName = "--- Number of books by genres ---";
        List<Map<String, Object>> queryResult = genreRepository.getNumberOfBooksByGenre();
        return new Report(reportName, convertToRows(queryResult));
    }

    private List<List<String>> convertToRows(List<Map<String, Object>> rowDataList) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("№", "Genre", "Number of books"));
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("name");
        fieldNames.add("number");
        int i = 0;
        for (Map<String, Object> data : rowDataList) {
            List<String> row = new ArrayList<>(3);
            row.add(String.valueOf(++i));
            for (String field : fieldNames) {
                row.add(data.get(field).toString());
            }
            rows.add(row);
        }
        return rows;
    }
}

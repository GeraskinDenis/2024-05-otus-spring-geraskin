package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import ru.otus.hw.dto.ReportDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.projections.NumberOfBooksByAuthor;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorReportServiceImpl implements AuthorReportService {

    private final AuthorRepository authorRepository;

    public AuthorReportServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public ReportDto countBooksByAuthors() {
        String reportName = "--- The number of books by authors ---";
        List<NumberOfBooksByAuthor> dataList = authorRepository.countBooksByAuthors();
        return new ReportDto(reportName, convertToRows(dataList));
    }

    @Override
    public ReportDto countBooksByAuthors(List<Long> authorIds) {
        String reportName = "--- The number of books by authors ---";
        List<NumberOfBooksByAuthor> dataList = authorRepository.countBooksByAuthors(authorIds);
        return new ReportDto(reportName, convertToRows(dataList));
    }

    private List<List<String>> convertToRows(List<NumberOfBooksByAuthor> dataList) {
        List<List<String>> rows = new ArrayList<>(dataList.size());
        rows.add(List.of("Author", "Number of books"));
        for (NumberOfBooksByAuthor data : dataList) {
            rows.add(List.of(data.getAuthorFullName(), data.getNumber().toString()));
        }
        return rows;
    }
}

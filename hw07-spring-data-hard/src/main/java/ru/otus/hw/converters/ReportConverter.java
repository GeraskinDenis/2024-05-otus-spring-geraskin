package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.Report;

import java.util.List;

@Component
public class ReportConverter {
    private final int columnLength = 20;

    public String reportToString(Report report) {
        StringBuilder builder = new StringBuilder(report.reportName())
                .append(System.lineSeparator());
        List<List<String>> rows = report.rows();
        for (List<String> row : rows) {
            for (String s : row) {
                builder.append(formatString(s));
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String formatString(String s) {
        return (s.length() < columnLength)
                ? s + " ".repeat(columnLength - s.length())
                : s.substring(0, columnLength);
    }
}
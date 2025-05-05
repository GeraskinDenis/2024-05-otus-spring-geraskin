package ru.otus.hw.dto;

import java.util.List;

public record ReportDto(String reportName, List<List<String>> rows) {
}

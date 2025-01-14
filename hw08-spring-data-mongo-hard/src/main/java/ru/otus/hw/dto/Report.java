package ru.otus.hw.dto;

import java.util.List;

public record Report(String reportName, List<List<String>> rows) {
}

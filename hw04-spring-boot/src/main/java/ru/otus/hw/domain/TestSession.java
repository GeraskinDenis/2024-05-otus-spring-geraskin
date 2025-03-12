package ru.otus.hw.domain;

import lombok.Data;

@Data
public class TestSession {

    private final Student student;

    private TestResult testResult;

}

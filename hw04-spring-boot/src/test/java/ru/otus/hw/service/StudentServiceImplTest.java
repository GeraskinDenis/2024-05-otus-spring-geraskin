package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.domain.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = StudentServiceImpl.class)
public class StudentServiceImplTest {

    @MockBean
    private LocalizedIOService localizedIOService;

    @Autowired
    private StudentServiceImpl studentService;

    @BeforeEach
    public void beforeEach() {
        when(localizedIOService.readStringWithPromptLocalized("StudentService.input.first.name"))
                .thenReturn("FirstNameTest");
        when(localizedIOService.readStringWithPromptLocalized("StudentService.input.last.name"))
                .thenReturn("LastNameTest");
    }

    @Test
    public void shouldDetermineCurrentStudentCorrectly() {
        Student expectedStudent = new Student("FirstNameTest", "LastNameTest");
        Student actualStudent = studentService.determineCurrentStudent(
                localizedIOService.readStringWithPromptLocalized("StudentService.input.first.name"),
                localizedIOService.readStringWithPromptLocalized("StudentService.input.last.name"));
        assertThat(actualStudent).isNotNull().isEqualTo(expectedStudent);
    }
}

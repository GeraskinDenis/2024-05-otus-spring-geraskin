package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.domain.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

	@Mock
	private LocalizedIOService localizedIOService;

	@InjectMocks
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
		Student actualStudent = studentService.determineCurrentStudent();
		assertThat(actualStudent).isNotNull().isEqualTo(expectedStudent);
	}
}

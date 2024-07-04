package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.domain.Student;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StudentServiceImplTest {
	private IOService ioService;

	private StudentService studentService;

	@BeforeEach
	public void beforeEach() {
		IOService ioService = mock(StreamsIOService.class);
		Mockito.when(ioService.readStringWithPrompt("Please input your first name:"))
				.thenReturn("FirstNameTest");
		Mockito.when(ioService.readStringWithPrompt("Please input your last name:"))
				.thenReturn("LastNameTest");
		studentService = new StudentServiceImpl(ioService);
	}

	@Test
	public void shouldDetermineCurrentStudentCorrectly() {
		Student expectedStudent = new Student("FirstNameTest", "LastNameTest");
		Student actualStudent = studentService.determineCurrentStudent();
		assertThat(actualStudent).isNotNull().isEqualTo(expectedStudent);
	}
}

package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
		ioService = new StreamsIOService(mock(PrintStream.class),
				new ByteArrayInputStream("FirstNameTest\nLastNameTest".getBytes()));
		studentService = new StudentServiceImpl(ioService);
	}

	@Test
	public void shouldDetermineCurrentStudentCorrectly() {
		Student expectedStudent = new Student("FirstNameTest", "LastNameTest");
		Student actualStudent = studentService.determineCurrentStudent();
		assertThat(actualStudent).isEqualTo(expectedStudent);
	}
}

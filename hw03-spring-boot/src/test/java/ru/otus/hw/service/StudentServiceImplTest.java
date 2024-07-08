package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.domain.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class StudentServiceImplTest {

	@Autowired
	LocalizedMessagesServiceImpl localizedMessagesService;

	@MockBean
	private StreamsIOService streamsIOService;

	@Autowired
	private StudentService studentService;

	@BeforeEach
	public void beforeEach() {
		when(streamsIOService.readStringWithPrompt(localizedMessagesService.getMessage("StudentService.input.first.name")))
				.thenReturn("FirstNameTest");
		when(streamsIOService.readStringWithPrompt(localizedMessagesService.getMessage("StudentService.input.last.name")))
				.thenReturn("LastNameTest");
	}

	@Test
	public void shouldDetermineCurrentStudentCorrectly() {
		Student expectedStudent = new Student("FirstNameTest", "LastNameTest");
		Student actualStudent = studentService.determineCurrentStudent();
		assertThat(actualStudent).isNotNull().isEqualTo(expectedStudent);
	}
}

package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.CreateStudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private StudentDtoMapper studentDtoMapper;
    @InjectMocks private StudentService studentService;

    @Test
    public void test_create_student_successfully() {
        CreateStudentDTO dto = new CreateStudentDTO();
        dto.setFirstName("Alice"); dto.setLastName("Martin"); dto.setEmail("alice@test.com");

        Student student = new Student();
        student.setFirstName("Alice"); student.setEmail("alice@test.com");

        when(studentRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(studentDtoMapper.toEntity(dto)).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student result = studentService.create(dto);

        verify(studentRepository).save(any(Student.class));
        assertThat(result.getEmail()).isEqualTo("alice@test.com");
    }

    @Test
    public void test_create_student_duplicate_email_throws_exception() {
        CreateStudentDTO dto = new CreateStudentDTO();
        dto.setEmail("alice@test.com");
        when(studentRepository.findByEmail("alice@test.com"))
            .thenReturn(Optional.of(new Student()));

        assertThrows(IllegalArgumentException.class, () -> studentService.create(dto));
    }

    @Test
    public void test_create_null_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> studentService.create(null));
    }

    @Test
    public void test_find_all() {
        when(studentRepository.findAll())
            .thenReturn(Arrays.asList(new Student(), new Student()));

        List<Student> result = studentService.findAll();
        assertThat(result).hasSize(2);
    }

    @Test
    public void test_find_by_id_success() {
        Student student = new Student();
        student.setId(1L); student.setFirstName("Alice");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student result = studentService.findById(1L);
        assertThat(result.getFirstName()).isEqualTo("Alice");
    }

    @Test
    public void test_find_by_id_not_found() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> studentService.findById(99L));
    }

    @Test
    public void test_update_student() {
        Student existing = new Student();
        existing.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(any())).thenReturn(existing);

        CreateStudentDTO dto = new CreateStudentDTO();
        dto.setFirstName("Updated"); dto.setLastName("Name"); dto.setEmail("a@b.com");

        studentService.update(1L, dto);

        verify(studentDtoMapper).updateEntity(dto, existing);
        verify(studentRepository).save(existing);
    }

    @Test
    public void test_delete_student() {
        Student student = new Student();
        student.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.delete(1L);

        verify(studentRepository).delete(student);
    }
}

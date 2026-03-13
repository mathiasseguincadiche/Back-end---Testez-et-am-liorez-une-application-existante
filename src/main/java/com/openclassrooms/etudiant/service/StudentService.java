package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.CreateStudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentDtoMapper studentDtoMapper;

    public Student create(CreateStudentDTO dto) {
        Assert.notNull(dto, "Student data must not be null");
        log.info("Creating student: {}", dto.getEmail());

        Optional<Student> existing = studentRepository.findByEmail(dto.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException(
                "Student with email " + dto.getEmail() + " already exists");
        }

        return studentRepository.save(studentDtoMapper.toEntity(dto));
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Student not found with id: " + id));
    }

    public Student update(Long id, CreateStudentDTO dto) {
        Assert.notNull(dto, "Student data must not be null");
        Student student = findById(id);
        studentDtoMapper.updateEntity(dto, student);
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        studentRepository.delete(findById(id));
        log.info("Deleted student id: {}", id);
    }
}

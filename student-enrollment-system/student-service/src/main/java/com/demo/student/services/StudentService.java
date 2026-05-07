package com.demo.student.services;

import com.demo.student.dtos.StudentRequest;
import com.demo.student.dtos.StudentResponse;
import com.demo.student.entities.Student;
import com.demo.student.exceptions.DuplicateStudentException;
import com.demo.student.exceptions.StudentNotFoundException;
import com.demo.student.repository.StudentRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        return toResponse(findStudentById(id));
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentByCnie(String cnie) {
        Student student = studentRepository.findByCnie(cnie)
                .orElseThrow(() -> new StudentNotFoundException("Student with CNIE " + cnie + " was not found"));
        return toResponse(student);
    }

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByCnie(request.getCnie())) {
            throw new DuplicateStudentException("CNIE " + request.getCnie() + " is already used");
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateStudentException("Email " + request.getEmail() + " is already used");
        }

        Student student = new Student(
                request.getCnie(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
        );
        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = findStudentById(id);

        studentRepository.findByCnie(request.getCnie())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateStudentException("CNIE " + request.getCnie() + " is already used");
                });

        studentRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateStudentException("Email " + request.getEmail() + " is already used");
                });

        student.setCnie(request.getCnie());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());

        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = findStudentById(id);
        studentRepository.delete(student);
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " was not found"));
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getCnie(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }
}

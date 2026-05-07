package com.demo.course.services;

import com.demo.course.dtos.CourseRequest;
import com.demo.course.dtos.CourseResponse;
import com.demo.course.entities.Course;
import com.demo.course.exceptions.CourseNotFoundException;
import com.demo.course.exceptions.DuplicateCourseException;
import com.demo.course.repository.CourseRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        return toResponse(findCourseById(id));
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateCourseException("Course title " + request.getTitle() + " is already used");
        }

        Course course = new Course(
                request.getTitle(),
                request.getDescription(),
                request.getCredits()
        );
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = findCourseById(id);

        courseRepository.findByTitle(request.getTitle())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateCourseException("Course title " + request.getTitle() + " is already used");
                });

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());

        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = findCourseById(id);
        courseRepository.delete(course);
    }

    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course with id " + id + " was not found"));
    }

    private CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCredits()
        );
    }
}

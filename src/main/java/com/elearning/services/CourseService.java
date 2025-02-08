package com.elearning.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elearning.models.Course;
import com.elearning.repositories.CourseRepository;

@Service
public class CourseService {

	 @Autowired
	 private CourseRepository courseRepository;
	
	 public void addCourse(Course course) {
		 courseRepository.save(course);
	    }
	 
}

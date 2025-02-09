package com.elearning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elearning.models.Course;
import com.elearning.services.CourseService;

@RestController
@RequestMapping("/course")
public class CourseController {

	@Autowired
	private CourseService courseService; 
	
	 @PostMapping("/add")
	    public ResponseEntity<String> addCourse(@RequestBody Course course) {
		 courseService.addCourse(course);
		 return ResponseEntity.status(HttpStatus.OK).body("Course Added Successfully" );
	 }
	 
	 @GetMapping("/getCourseName/{id}")
	 public ResponseEntity<String> getNameById(@PathVariable Long id) {
		 try {
		String CourseName=courseService.getCourseName(id);
		return ResponseEntity.status(HttpStatus.OK).body(CourseName );
		 }
		 catch (Exception e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course Not Found" );
		}
		
	 }
	
	
}

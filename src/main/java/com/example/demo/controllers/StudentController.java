package com.example.demo.controllers;

import com.example.demo.entities.Student;
import com.example.demo.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/")
public class StudentController {

    //Use the ThreadLocalRandom class to generate random numbers
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    @Autowired
    private StudentRepository studentRepository;


    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Student student) {
        try {
            //Generate ID
            int generatedId = generateId();
            //Set the generated ID
            student.setId(generatedId);
            // Save student
            studentRepository.save(student);

            // Returns the generated ID to the user
            return ResponseEntity.ok("Student saved. Generated ID: " + generatedId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("information incomplete");
        }
    }

    // A method to generate a unique ID
    private int generateId() {

        return random.nextInt(10000);
    }


    @GetMapping("/get")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }


    @GetMapping("/getById/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        Student student = studentRepository.findById(id)
                .orElse(null);

        if (student != null) {
            return ResponseEntity.ok(student);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Student()); // Returns an empty Student object
        }
    }

    //student is the object entered by the user
    //currentStudent is the object in the database
    @PutMapping("/updateById/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable int id, @RequestBody Student student) {
        Student currentStudent = studentRepository.findById(id)
                .orElse(null);

        if (currentStudent == null) {
            return ResponseEntity.ok("Student with id " + id + " not found");
        }

        try {
            currentStudent.setId(id);
            currentStudent.setName(student.getName());
            currentStudent.setEmail(student.getEmail());
            currentStudent.setDob(student.getDob());


            studentRepository.save(currentStudent);
            studentRepository.flush();
            return ResponseEntity.ok("Student with id " + id + " updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("information incomplete");
        }
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> deleteById(@PathVariable int id) {
        Student student = studentRepository.findById(id)
                .orElse(null);

        if (student == null) {
            return ResponseEntity.ok("Student with id " + id + " not found");
        }
        studentRepository.delete(student);

        return ResponseEntity.ok("Student with id " + id + " deleted successfully");
    }

}

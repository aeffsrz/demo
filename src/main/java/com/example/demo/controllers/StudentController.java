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

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    @Autowired
    private StudentRepository studentRepository;


    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Student student) {
        try {
            // 生成ID
            int generatedId = generateId(); // 自定义方法，用于生成唯一ID

            // 设置生成的ID
            student.setId(generatedId);

            // 保存Student
            studentRepository.save(student);

            // 返回生成的ID给用户
            return ResponseEntity.ok("Student saved. Generated ID: " + generatedId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("information incomplete");
        }
    }

    // 生成唯一ID的方法
    private int generateId() {

        return random.nextInt(10000); // 生成一个随机数作为ID，实际应用中需要保证唯一性
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
                    .body(new Student()); // 返回500错误和空的Student对象
        }
    }

    //student 是用户输入的对象
    //currentStudent 是数据库中的对象
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

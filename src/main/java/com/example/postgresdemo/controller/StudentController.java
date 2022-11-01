package com.example.postgresdemo.controller;

import com.example.postgresdemo.entity.Student;
import com.example.postgresdemo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/student")
public class StudentController {

    @Autowired
    private StudentService service;

    @GetMapping
//    @PreAuthorize("hasAuthority('ACCESS_STUDENT')")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(service.getAllStudents());
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('CREATE_STUDENT')")
    public ResponseEntity<Student> create(@RequestBody Student student){
        return ResponseEntity.status(201).body(service.create(student));
    }

    @GetMapping("/city")
    public ResponseEntity<List<Student>> getByCityName(@RequestParam("city") String city) {
        return ResponseEntity.ok(service.getByCity(city));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable("id") Integer id){
        return ResponseEntity.ok(service.getById(id).orElse(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable("id") Integer id, @RequestBody Student student) {
        return ResponseEntity.ok(service.update(id, student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

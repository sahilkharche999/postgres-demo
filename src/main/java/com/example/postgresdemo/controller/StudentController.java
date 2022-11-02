package com.example.postgresdemo.controller;

import com.example.postgresdemo.config.CustomAuthenticationProvider;
import com.example.postgresdemo.dto.AuthToken;
import com.example.postgresdemo.dto.LoginRequest;
import com.example.postgresdemo.entity.Student;
import com.example.postgresdemo.service.StudentDetails;
import com.example.postgresdemo.service.StudentDetailsService;
import com.example.postgresdemo.service.StudentService;
import com.example.postgresdemo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/student")
public class StudentController {

    @Autowired
    private StudentService service;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StudentDetailsService studentDetailsService;

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

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

    @PostMapping("/login")
    public ResponseEntity<AuthToken> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationProvider.authenticateUser(request));
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String refreshToken = header.replace("Bearer ", "");
            String username = null;

            try {
                username = jwtUtil.getUsernameFromToken(refreshToken);
            } catch (Exception ignored) {}

            if (username != null) {
                StudentDetails studentDetails = (StudentDetails) studentDetailsService.loadUserByUsername(username);
                String accessToken = jwtUtil.generateTokenFromUserDetails(studentDetails);
                Map<String, String> token = new HashMap<>();
                token.put("accessToken", accessToken);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), token);
             }
        } else {
            Map<String, String> res = new HashMap<>();
            res.put("detail", "Token not found");
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), res);
        }
    }

}

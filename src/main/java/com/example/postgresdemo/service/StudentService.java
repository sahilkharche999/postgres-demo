package com.example.postgresdemo.service;

import com.example.postgresdemo.entity.Student;
import com.example.postgresdemo.repository.AddressRepository;
import com.example.postgresdemo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;

    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    public Student create(Student student){
        return studentRepository.save(student);
    }

    public Optional<Student> getById(Integer id){
        return studentRepository.findById(id);
    }

    public Student update(Integer id, Student student){
        Student record = studentRepository.findById(id).orElse(null);
        if (record != null) {
            record.setName(student.getName());
            record.setAge(student.getAge());
            record.setStandard(student.getStandard());
            record.setEmail(student.getEmail());
            return studentRepository.save(record);
        }
        return null;
    }

    public void deleteById(Integer id) {
        studentRepository.deleteById(id);
    }


}

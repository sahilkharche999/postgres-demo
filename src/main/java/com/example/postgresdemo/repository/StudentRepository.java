package com.example.postgresdemo.repository;

import com.example.postgresdemo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByAgeGreaterThanEqual(Integer age);

    long countByNameLike(String name);

    @Query(value = "select s from Student s where s.age <= :age")
    List<Student> customQuery(@Param("age") int age);

    List<Student> findByAddressCityEqualsIgnoreCase(String city);

    Optional<Student> findByName(String name);

}

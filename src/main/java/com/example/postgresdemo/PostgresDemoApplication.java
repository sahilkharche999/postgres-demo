package com.example.postgresdemo;

import com.example.postgresdemo.entity.Address;
import com.example.postgresdemo.entity.Student;
import com.example.postgresdemo.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class PostgresDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostgresDemoApplication.class, args);
	}

	@Bean
	public PasswordEncoder encode(){
		return new BCryptPasswordEncoder(8);
	}

}

package com.example.postgresdemo.config;

import com.example.postgresdemo.dto.AuthToken;
import com.example.postgresdemo.dto.LoginRequest;
import com.example.postgresdemo.service.StudentDetails;
import com.example.postgresdemo.service.StudentDetailsService;
import com.example.postgresdemo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {


    private final JwtUtil jwtUtil;

    public CustomAuthenticationProvider(JwtUtil jwtUtil, PasswordEncoder encode, StudentDetailsService studentDetailsService) {
        this.jwtUtil = jwtUtil;
        this.setPasswordEncoder(encode);
        this.setUserDetailsService(studentDetailsService);
    }


    public AuthToken authenticateUser(LoginRequest request) {
        Authentication authentication = authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        StudentDetails studentDetails = (StudentDetails) authentication.getPrincipal();
        if (studentDetails != null && studentDetails.getStudent() != null) {
            String accessToken = jwtUtil.generateTokenFromUserDetails(studentDetails);
            String refreshToken = jwtUtil.generateRefreshToken(studentDetails);
            String name = studentDetails.getStudent().getName();
            return new AuthToken(accessToken, refreshToken, name);
        }
        return null;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return super.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

package com.example.postgresdemo.filter;

import com.example.postgresdemo.service.StudentDetails;
import com.example.postgresdemo.service.StudentDetailsService;
import com.example.postgresdemo.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@NoArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String HEADER_PREFIX = "Bearer ";
    private final String HEADER_NAME = "Authorization";
    @Autowired
    private StudentDetailsService studentDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HEADER_NAME);
        String username = null;
        String authToken = null;
        if (!request.getRequestURI().equalsIgnoreCase("/student/login")) {
            if (header != null && header.startsWith(HEADER_PREFIX)) {
                authToken = header.replace(HEADER_PREFIX, "");
                try {
                    username = jwtUtil.getUsernameFromToken(authToken);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getLocalizedMessage());
                } catch (ExpiredJwtException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Jwt token expired");
                } catch (SignatureException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid credentials");
                }
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                StudentDetails studentDetails = (StudentDetails) studentDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(authToken, studentDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(studentDetails, null,
                                    List.of(new SimpleGrantedAuthority("ACCESS_USER"), new SimpleGrantedAuthority("CREATE_USER")));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

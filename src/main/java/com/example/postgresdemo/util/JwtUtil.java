package com.example.postgresdemo.util;

import com.example.postgresdemo.service.StudentDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.token.accessToken.expirationTime}")
    private long accessTokenExpiration;

    @Value("${jwt.token.refreshToken.expirationTime}")
    private long refreshTokenExpiration;


    @Value("${jwt.token.signingKey}")
    private String signingKey;

    public String generateTokenFromUserDetails(StudentDetails studentDetails) {
        return generateToken(studentDetails.getStudent().getName(), (List<SimpleGrantedAuthority>) studentDetails.getAuthorities());
    }

    //access token
    private String generateToken(String subject, List<SimpleGrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("scope", authorities);
        return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000))
                .signWith(SignatureAlgorithm.HS256, signingKey).compact();
    }

    public String generateRefreshToken(StudentDetails studentDetails) {
        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) studentDetails.getAuthorities();
        Claims claims = Jwts.claims().setSubject(studentDetails.getStudent().getName());
        claims.put("scope", authorities);
        return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration * 1000))
                .signWith(SignatureAlgorithm.HS256, signingKey).compact();
    }

    public boolean validateToken(String token, StudentDetails studentDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equalsIgnoreCase(studentDetails.getStudent().getName()) &&
                !Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody()
                        .getExpiration().before(new Date(System.currentTimeMillis())));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }
}

package com.authservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    private static final String SECRET_KEY = "jwt-secret";
//    private static final String TOKEN_PREFIX = "Bearer ";
//    private static final String HEADER_STRING = "Authorization";
    private static final long EXPIRY_TIME = 864_000_000;

    public String generateToken(String username, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRY_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String validateTokenAndRetrieveUser(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token)
                .getSubject();
    }

}

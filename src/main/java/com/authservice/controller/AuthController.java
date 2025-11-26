package com.authservice.controller;

import com.authservice.dto.APIResponse;
import com.authservice.dto.LoginDto;
import com.authservice.dto.UserDto;
import com.authservice.service.AuthService;
import com.authservice.service.JWTService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> registerUser(@RequestBody UserDto userDto) {
        APIResponse<String> response = authService.registerUser(userDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> loginUser(@RequestBody LoginDto loginDto) {
        APIResponse<String> response = new APIResponse<>();
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        try {
            Authentication authenticated = authenticationManager.authenticate(token);
            if (authenticated.isAuthenticated()) {
                String jwtToken = jwtService.generateToken(loginDto.getUsername(), authenticated.getAuthorities().iterator().next().getAuthority());

                response.setMessage("Login successful");
                response.setStatusCode(200);
//                response.setData("User " + loginDto.getUsername() + " logged in successfully");
                response.setData(jwtToken);
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        // if authentication fails return failed message
        response.setMessage("Login Failed.");
        response.setStatusCode(401);
        response.setData("Invalid username or password. Try again.");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatusCode()));
    }
}

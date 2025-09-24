package org.authservice.controller;

import org.authservice.dto.ApiResponse;
import org.authservice.dto.LoginDto;
import org.authservice.dto.UserDto;
import org.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authManager;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authManager) {
        this.authService = authService;
        this.authManager = authManager;
    }

    @PostMapping("/register-user")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody UserDto dto) {
        ApiResponse<String> response = authService.registerUser(dto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginAuthenticatedUser(@RequestBody LoginDto dto) {
        ApiResponse<String> response = new ApiResponse<>();

        // Create a token containing the username and password entered by the user.
        // This token will be passed to the AuthenticationManager to verify credentials.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        try {
            // Delegate authentication to the AuthenticationManager.
            // This will internally:
            // - Use the configured AuthenticationProvider (e.g., DaoAuthenticationProvider)
            // - That will call your CustomUserDetailService.loadUserByUsername()
            // - Then compare the password using the PasswordEncoder (BCrypt)
            Authentication authenticatedUser = authManager.authenticate(token);
            // If the user is successfully authenticated, return a 200 OK response
            if (authenticatedUser.isAuthenticated()) {
                response.setMessage("Login Successful");
                response.setStatus(200);
                response.setData("User has logged in successfully");
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
            }
            // If any exception occurs during authentication (e.g., bad credentials),
            // catch it and respond with an Unauthorized (401) status.
        } catch (AuthenticationException e) { // For debugging; remove in production.
            e.printStackTrace();
        }

        // Return a failure response if authentication failed
        response.setMessage("Login Failed");
        response.setStatus(401);
        response.setData("Unauthorized Access: Invalid Username or Password");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

}

package org.authservice.service;

import org.authservice.dto.ApiResponse;
import org.authservice.dto.UserDto;
import org.authservice.entity.UserEntity;
import org.authservice.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponse<String> registerUser(@RequestBody UserDto dto) {
        // create object of ApiResponse
        ApiResponse<String> response = new ApiResponse();

        if (userRepository.existsByUsername(dto.getUsername())) {
            response.setMessage(dto.getUsername() + " already exists");
            response.setStatus(500);
            response.setData("This username is  already taken. Please try again");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            response.setMessage(dto.getEmail() + " already exists");
            response.setStatus(500);
            response.setData("This email is  already taken. Please try again");
        }
        // encode the password before saving that to the database
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(encryptedPassword);
        // finally save the user and return response as APIResponse
        UserEntity savedUser = userRepository.save(user);
        if (savedUser == null) {
            throw new RuntimeException("Something went wrong. Unable to save user.");
        }
        response.setMessage("User registered successfully.");
        response.setStatus(201);
        response.setData(dto.getUsername() + " has been successfully registered.");
        return response;
    }
}

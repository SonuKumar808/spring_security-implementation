package com.authservice.service;

import com.authservice.dto.APIResponse;
import com.authservice.dto.UserDto;
import com.authservice.entity.User;
import com.authservice.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public APIResponse<String> registerUser(UserDto userDto) {
        // 0. create object of APIResponse
        APIResponse<String> apiResponse = new APIResponse<>();
        // 1. Check whether user exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            apiResponse.setMessage("Registration Failed!!!");
            apiResponse.setStatusCode(500);
            apiResponse.setData("User with " + userDto.getUsername() + " is already exists. Please choose another username");
            return apiResponse;
        }
        // 2. check whether email exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            apiResponse.setMessage("Registration Failed!!!");
            apiResponse.setStatusCode(500);
            apiResponse.setData("User with " + userDto.getEmail() + " is already exists. Please select another email");
            return apiResponse;
        }
        // 3. encode the password before saving that to database
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setPassword(encodedPassword);
        user.setRole("ROLE_ADMIN");
        User savedUser = userRepository.save(user);
        if (savedUser == null) {
            // custom exception
        }
        apiResponse.setMessage("Registration Successful!!!");
        apiResponse.setStatusCode(201);
        apiResponse.setData("User registered successfully");
        return apiResponse;
        // 4. save the user and return the response as APIResponse
//        return new APIResponse<>();
    }

}

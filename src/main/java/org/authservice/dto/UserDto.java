package org.authservice.dto;

import lombok.Data;

@Data
public class UserDto {

    private long id;
    private String name;
    private String username;
    private String email;
    private String password;
}

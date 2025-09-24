package org.authservice.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private String message;
    private int status;
    private T data;

}

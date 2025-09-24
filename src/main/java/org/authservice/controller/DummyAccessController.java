package org.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dummy")
public class DummyAccessController {

    @GetMapping("/access-msg")
    public String authorizationMessage() {
        return "Authorization required to access this resource";
    }
}

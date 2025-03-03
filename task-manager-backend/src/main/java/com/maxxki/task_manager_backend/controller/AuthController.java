package com.maxxki.task_manager_backend.controller;

import com.maxxki.task_manager_backend.model.PinkyUser; // Import zu PinkyUser geändert!
import com.maxxki.task_manager_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody PinkyUser user) { // Parameter-Typ zu PinkyUser geändert!
        return authService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody PinkyUser user) { // Parameter-Typ zu PinkyUser geändert!
        return authService.loginUser(user);
    }
}
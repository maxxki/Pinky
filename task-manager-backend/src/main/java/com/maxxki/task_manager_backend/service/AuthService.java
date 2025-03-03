package com.maxxki.task_manager_backend.service;

import com.maxxki.task_manager_backend.model.PinkyUser; // Import zu PinkyUser geändert!
import com.maxxki.task_manager_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public ResponseEntity<String> registerUser(PinkyUser user) { // Parameter-Typ zu PinkyUser geändert!
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<String> loginUser(PinkyUser user) { // Parameter-Typ zu PinkyUser geändert!
        PinkyUser existingUser = userRepository.findByUsername(user.getUsername()).orElse(null); // Typ zu PinkyUser geändert!
        if (existingUser == null) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("Login successful", HttpStatus.OK);
    }
}
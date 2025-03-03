package com.example.maxxki;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class APIController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hallo von MaxxKI!";
    }
}

package com.deinprojektname.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AngularController {

    @GetMapping("/{path:[^\\.]*}")
    public String forwardAngularRoutes() {
        return "forward:/index.html";
    }
}

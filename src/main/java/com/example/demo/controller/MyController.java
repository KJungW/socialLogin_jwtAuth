package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/my")
    @ResponseBody
    public String myAPI() {
        return "my route";
    }
}
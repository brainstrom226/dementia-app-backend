package com.db.dementia.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    @GetMapping(path = "/test")
    public String test() {
        return "Successful";
    }
}

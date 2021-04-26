package com.sinqia.sqspii.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusResource {

    @GetMapping(path = "/")
    public String status() {
        return "its all ok";
    }
}

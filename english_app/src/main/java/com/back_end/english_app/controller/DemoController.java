package com.back_end.english_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/")
public class DemoController {
    @GetMapping("demo")
    public ResponseEntity<String> DemoController(){
        return ResponseEntity.ok("Api chạy ổn !");
    }
}

package com.back_end.english_app.controller;

import com.back_end.english_app.config.APIResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/")
public class DemoController {
    @GetMapping("demo")
    public APIResponse<String> DemoController(){
        return APIResponse.<String>builder()
                .result(" Tôi đang test api ")
                .build();
    }
}

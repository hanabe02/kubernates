package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {
    @GetMapping("/") // / 주소로 요청이 오면 hello world 응답 하겠다는 의미
    public String home() {
        return "Hello World";
    }
}

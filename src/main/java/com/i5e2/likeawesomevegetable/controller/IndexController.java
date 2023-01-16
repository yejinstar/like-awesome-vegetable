package com.i5e2.likeawesomevegetable.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {
    @GetMapping("")
    public String testIndex() {
        return "ci/cd 테스트";
    }
}

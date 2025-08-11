package com.example.InfoMax;

import org.springframework.web.bind.annotation.RequestMapping;

public class HomeController {

    @RequestMapping("/")
    public String search() {
        return "index.html";
    }

}

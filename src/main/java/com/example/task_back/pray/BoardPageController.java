package com.example.task_back.pray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardPageController {

    @GetMapping("/home")
    public String showPage(){

        return "home";
    }

}

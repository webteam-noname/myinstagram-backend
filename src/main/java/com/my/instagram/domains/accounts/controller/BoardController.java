package com.my.instagram.domains.accounts.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoardController {

    @GetMapping("/api/user/index")
    public String index(){
        return "index";
    }

}

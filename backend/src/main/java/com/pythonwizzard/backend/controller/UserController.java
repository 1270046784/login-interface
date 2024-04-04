package com.pythonwizzard.backend.controller;

import com.pythonwizzard.backend.entity.RestBean;
import com.pythonwizzard.backend.entity.user.NormalUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/info")
    public RestBean<NormalUser> getUserInfo(@SessionAttribute("user") NormalUser user) {
        return RestBean.success(user);
    }

}

package com.pythonwizzard.backend.controller;

import com.pythonwizzard.backend.entity.RestBean;
import com.pythonwizzard.backend.entity.user.NormalUser;
import com.pythonwizzard.backend.service.UserDirService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserDirService service;

    @GetMapping("/info")
    public RestBean<NormalUser> getUserInfo(
            @SessionAttribute("user") NormalUser user
    ) {
        user.setUserDir(service.getUserDir(user.getUsername()));
        return RestBean.success(user);
    }

    @PostMapping("/create-file")
    public RestBean<String> createFile(
            @RequestParam("username") String username,
            @RequestParam("title") String title
    ) {
        service.createFile(username, title);
        return RestBean.success("成功创建文件");
    }

}

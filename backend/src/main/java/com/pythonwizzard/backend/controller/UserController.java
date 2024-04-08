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

    @PostMapping("/change-title")
    public RestBean<String> changeTitle(
            @RequestParam("username") String username,
            @RequestParam("oldTitle") String oldTitle,
            @RequestParam("newTitle") String newTitle
    ) {
        service.changeTitle(username, oldTitle, newTitle);
        return RestBean.success("成功重命名文件");
    }

    @PostMapping("remove-file")
    public RestBean<String> removeFile(
            @RequestParam("username") String username,
            @RequestParam("title") String title
    ) {
        service.removeFile(username, title);
        return RestBean.success("成功删除文件");
    }

    @PostMapping("save-file")
    public RestBean<String> saveFile(
            @RequestParam("username") String username,
            @RequestParam("title") String title,
            @RequestParam("text") String text
    ) {
        service.saveFile(username, title, text);
        return RestBean.success("成功保存文件");
    }

}

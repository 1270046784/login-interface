package com.pythonwizzard.backend.entity.user;

import lombok.Data;

import java.util.List;

@Data
public class NormalUser {

    private int id;
    private String username;
    private String email;
    private List<TextFile> userDir;

}

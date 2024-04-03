package com.pythonwizzard.backend.entity;

import lombok.Data;

@Data
public class Account {

    private int id;
    private String username;
    private String password;
    private String email;
}

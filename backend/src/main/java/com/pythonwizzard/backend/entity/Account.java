package com.pythonwizzard.backend.entity;

import lombok.Data;

/**
 * 服务器端用户信息
 */
@Data
public class Account {

    private int id;
    private String username;
    private String password;
    private String email;

}

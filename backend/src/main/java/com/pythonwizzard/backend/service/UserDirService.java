package com.pythonwizzard.backend.service;

import com.pythonwizzard.backend.entity.user.TextFile;

import java.util.List;

public interface UserDirService {

    List<TextFile> getUserDir(String username);
    void createFile(String username, String title);
}

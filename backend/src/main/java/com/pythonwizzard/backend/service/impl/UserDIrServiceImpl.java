package com.pythonwizzard.backend.service.impl;

import com.pythonwizzard.backend.dao.UserDirDao;
import com.pythonwizzard.backend.entity.user.TextFile;
import com.pythonwizzard.backend.service.UserDirService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDIrServiceImpl implements UserDirService {

    @Resource
    private UserDirDao userDirDao;

    @Override
    public List<TextFile> getUserDir(String username) {
        String tableName = username + "_dir";
        userDirDao.createDirTable(username);
        return userDirDao.getUserDir(tableName);
    }

    @Override
    public void createFile(String username, String title) {
        String tableName = username + "_dir";
        userDirDao.createFile(tableName, title);
    }

    @Override
    public void changeTitle(String username, String oldTitle, String newTitle) {
        String tableName = username + "_dir";
        userDirDao.changeTitle(tableName, oldTitle, newTitle);
    }

    @Override
    public void removeFile(String username, String title) {
        String tableName = username + "_dir";
        userDirDao.removeFile(tableName, title);
    }

    @Override
    public void saveFile(String username, String title, String text) {
        String tableName = username + "_dir";
        userDirDao.saveFile(tableName, title, text);
    }

}

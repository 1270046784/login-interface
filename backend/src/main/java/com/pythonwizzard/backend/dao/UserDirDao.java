package com.pythonwizzard.backend.dao;

import com.pythonwizzard.backend.entity.user.TextFile;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDirDao {

    // 动态创建用户目录的表
    // 这种方式可能存在注入问题
    @Update("create table login.${tableName} (" +
            "`title` varchar(255) primary key, " +
            "`text` longtext not null" +
            ") engine = InnoDB default charset=utf8mb4;"
    )
    void createDynamicTable(@Param("tableName") String tableName);

    @Insert("insert into login.${tableName} (title, text) values ('hello', 'welcome to use Unnamed Markdown Editor')")
    void initUserDir(@Param("tableName") String tableName);

    @Select("select * from login.${tableName}")
    List<TextFile> getUserDir(@Param("tableName") String tableName);

    @Insert("insert into login.${tableName} (title, text) values (#{title}, '')")
    void createNewFile(@Param("tableName") String tableName, String title);

    default boolean createDirTable(String username) {
        String tableName = username + "_dir";
        try {
            createDynamicTable(tableName);
            initUserDir(tableName);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

}

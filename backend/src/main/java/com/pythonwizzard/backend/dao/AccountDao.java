package com.pythonwizzard.backend.dao;

import com.pythonwizzard.backend.entity.auth.Account;
import com.pythonwizzard.backend.entity.user.NormalUser;
import com.pythonwizzard.backend.entity.user.TextFile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AccountDao {

    @Select("select * from login.accounts where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmails(String text);

    @Select("select * from login.accounts where username = #{text} or email = #{text}")
    NormalUser findNormalUserByNameOrEmails(String text);

    @Insert("insert into login.accounts (username, password, email) values (#{username}, #{password}, #{email})")
    int registerAccount(String username, String password, String email);

    @Update("update login.accounts set password = #{password} where email = #{email}")
    int updateAccountPasswordByEmail(String email, String password);

    // 检查对应表名是否存在
    @Select("select count(*) from login.#{tableName}")
    int checkTableExists(String tableName);

    // 动态创建用户目录的表
    // 这种方式可能存在注入问题
    @Update("create table login.#{tableName} (" +
            "`title` varchar(255) primary key, " +
            "`text` longtext not null" +
            ") engine = InnoDB default charset=utf8mb4;"
    )
    void createDynamicTable(String tableName);

    @Select("select * from login.#{tableName}")
    List<TextFile> getTextFile(String tableName);

    default boolean createDirTable(String username) {
        String tableName = username + "_dir";
        if (checkTableExists(tableName) == 0) {
            createDynamicTable(tableName);
            return true;
        }
        return false;
    }
}

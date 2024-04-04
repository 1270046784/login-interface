package com.pythonwizzard.backend.mapper;

import com.pythonwizzard.backend.entity.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper {

    @Select("select * from login.accounts where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmails(String text);

    @Insert("insert into login.accounts (username, password, email) values (#{username}, #{password}, #{email})")
    int registerAccount(String username, String password, String email);

}

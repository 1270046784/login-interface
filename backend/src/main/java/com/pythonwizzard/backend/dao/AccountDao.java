package com.pythonwizzard.backend.dao;

import com.pythonwizzard.backend.entity.auth.Account;
import com.pythonwizzard.backend.entity.user.NormalUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

}

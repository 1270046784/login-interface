package com.cy.backend.mapper;

import com.cy.backend.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名或邮箱查询用户数据
     * @param text 用户名或邮箱
     * @return 用户对象
     */
    @Select("select * from login.accounts where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmails(String text);
}

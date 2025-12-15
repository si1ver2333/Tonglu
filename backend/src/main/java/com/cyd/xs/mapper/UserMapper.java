package com.cyd.xs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.User.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 登录查询
     */
    @Select("SELECT * FROM users WHERE username = #{username} LIMIT 1")
    User selectByUsername(@Param("username") String username);

    /**
     * 用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    Integer countByUsername(@Param("username") String username);

    /**
     * ⭐ 按 ID 查询（兼容旧代码）
     */
    @Select("SELECT * FROM users WHERE id = #{userId}")
    User findById(@Param("userId") Long userId);

    /**
     * MyBatis-Plus 自带 selectById，也保留
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(@Param("id") Long id);

    /**
     * 查询用户角色
     */
    @Select("SELECT role FROM users WHERE id = #{id}")
    String getUserRole(@Param("id") Long id);

    /**
     * 查询昵称
     */
    @Select("SELECT display_name FROM users WHERE id = #{id}")
    String getUserDisplayName(@Param("id") Long id);
}

package com.kdyzm.spring.security.auth.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kdyzm.spring.security.auth.center.entity.TUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author kdyzm
 */
public interface UserMapper extends BaseMapper<TUser> {

    @Select("SELECT DISTINCT tp.`code` FROM `t_user_role` tur \n" +
            "INNER JOIN `t_role_permission` trp ON tur.`role_id` = trp.`role_id`\n" +
            "INNER JOIN `t_permission` tp ON trp.`permission_id` = tp.`id`\n" +
            "WHERE tur.`user_id` = #{userId};")
    List<String> findAllPermissions(@Param("userId") Integer userId);
}

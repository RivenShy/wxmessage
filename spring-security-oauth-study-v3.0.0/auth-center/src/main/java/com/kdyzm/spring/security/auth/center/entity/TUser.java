package com.kdyzm.spring.security.auth.center.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author kdyzm
 */
@Data
@TableName("t_user")
public class TUser {

    private Integer id;

    private String username;

    private String password;

    private String fullname;

    private String mobile;
}

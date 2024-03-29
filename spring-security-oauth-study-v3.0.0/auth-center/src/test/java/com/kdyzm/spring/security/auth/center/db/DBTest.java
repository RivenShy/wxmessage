package com.kdyzm.spring.security.auth.center.db;

import com.kdyzm.spring.security.auth.center.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author kdyzm
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DBTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        log.info(("----- selectAll method test ------"));
        userMapper.selectList(null);
    }
}

package com.example.mybatplusdemo;

import com.example.mybatplusdemo.mapper.UserMapper;
import com.example.mybatplusdemo.model.User;
import com.example.mybatplusdemo.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@MybatisPlusTest
@SpringBootTest
class MybatisPlusSampleTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IUserService userService;


    @Test
    void testInsert() {
        User sample = new User();
        sample.setAge(1);
        sample.setEmail("1");
        sample.setNameXXX("1");
        userMapper.insert(sample);
        assertThat(sample.getId()).isNotNull();
    }

    @Test
    void testSave() {
        User sample = new User();
        sample.setAge(1);
        sample.setEmail("1");
        sample.setNameXXX("1");
        userService.save(sample);
        assertThat(sample.getId()).isNotNull();
    }

    @Test
    void saveBatch() {
        List<User> list = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            User sample = new User();
            sample.setAge(i);
            sample.setEmail("" + i);
            sample.setNameXXX("" + i);
            list.add(sample);
        }
        userService.saveBatch(list);
    }

    @Test
    void saveOrUpdate() {
        User sample = new User();
//        sample.setId(1L);
        sample.setAge(1);
        sample.setEmail("1u");
        sample.setNameXXX("1u");
        userService.saveOrUpdate(sample);
        assertThat(sample.getId()).isNotNull();
    }

    @Test
    void saveOrUpdateBatch() {
        List<User> list = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            User sample = new User();
            sample.setId(i+3L);
            sample.setAge(i);
            sample.setEmail("" + i);
            sample.setNameXXX("" + i);
            list.add(sample);
        }
        userService.saveOrUpdateBatch(list);
    }

    @Test
    void removeById() {
        User sample = new User();
        sample.setId(6L);
        sample.setAge(1);
        sample.setEmail("" + 1);
        sample.setNameXXX("" + 1);
        userService.updateById(sample);
        assertThat(sample.getId()).isNotNull();
    }

    @Test
    void getById() {
        User sample = userService.getById(1);
        assertThat(sample.getId()).isNotNull();
        System.out.println(sample);
    }

    @Test
    void list() {
        List<User> userList = userService.list();
        userList.forEach(System.out::println);
    }

//    @Test
//    void page() {
//        List<User> userList = userService.page();
//        userList.forEach(System.out::println);
//    }

    @Test
    void page() {
        long count = userService.count();
        System.out.println("count:" + count);
    }
}
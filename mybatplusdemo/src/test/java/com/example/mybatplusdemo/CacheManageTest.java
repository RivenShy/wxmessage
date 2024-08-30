package com.example.mybatplusdemo;


import com.example.mybatplusdemo.config.CacheUtil;
import com.example.mybatplusdemo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CacheManageTest {

    @Test
    public void testCache() {
        User userGet = CacheUtil.get("demo:user", "user:id:", "1", () -> {
//			R<User> result = getUserClient().userInfoById(userId);
//			return result.getData();
            User user = new User();
            user.setId(1l);
            user.setNameXXX("张三");
            return user;
        });
        System.out.println(userGet);
        User userGet2 = CacheUtil.get("demo:user", "user:id:", "1", () -> {
//			R<User> result = getUserClient().userInfoById(userId);
//			return result.getData();
            User user = new User();
            user.setId(1l);
            user.setNameXXX("张三");
            return user;
        });
        System.out.println(userGet2);
        User userGet3 = CacheUtil.get("demo:user", "user:id:", "1", () -> {
//			R<User> result = getUserClient().userInfoById(userId);
//			return result.getData();
            User user = new User();
            user.setId(1l);
            user.setNameXXX("张三");
            return user;
        });
        System.out.println(userGet3);
        CacheUtil.clear("demo:user");
        User userGet4 = CacheUtil.get("demo:user", "user:id:", "1", () -> {
//			R<User> result = getUserClient().userInfoById(userId);
//			return result.getData();
            User user = new User();
            user.setId(1l);
            user.setNameXXX("张三");
            return user;
        });
        System.out.println(userGet4);
    }
}

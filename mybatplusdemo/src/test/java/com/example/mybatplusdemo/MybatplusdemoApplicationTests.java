package com.example.mybatplusdemo;

import com.example.mybatplusdemo.config.CacheUtil;
import com.example.mybatplusdemo.mapper.UserMapper;
import com.example.mybatplusdemo.model.User;
import com.example.mybatplusdemo.utils.R;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MybatplusdemoApplicationTests {

	@Autowired
	private UserMapper userMapper;

	@Test
	void contextLoads() {
	}

	@Test
	public void testSelect() {
		System.out.println(("----- selectAll method test ------"));
		List<User> userList = userMapper.selectList(null);
//		Assert.assertEquals(5, userList.size());
		userList.forEach(System.out::println);
	}

	@Test
	public void testCache() {
		User userGet = CacheUtil.get("demo:user", "user:name:", "1", () -> {
//			R<User> result = getUserClient().userInfoById(userId);
//			return result.getData();
			User user = new User();
			user.setId(1l);
			user.setNameXXX("张三");
			return user;
		});
		System.out.println(userGet);
		User userGet2 = CacheUtil.get("demo:user", "user:name:", "1", () -> {
//			R<User> result = getUserClient().userInfoById(userId);
//			return result.getData();
			User user = new User();
			user.setId(1l);
			user.setNameXXX("张三");
			return user;
		});
		System.out.println(userGet2);
	}
}

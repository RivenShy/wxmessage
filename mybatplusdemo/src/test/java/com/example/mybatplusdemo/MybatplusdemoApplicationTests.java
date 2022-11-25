package com.example.mybatplusdemo;

import com.example.mybatplusdemo.mapper.UserMapper;
import com.example.mybatplusdemo.model.User;
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
}

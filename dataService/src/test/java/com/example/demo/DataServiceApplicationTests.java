package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.mapper.PendingApprovalMapper;
import com.example.demo.service.IPendingApprovalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;

@SpringBootTest
class DataServiceApplicationTests {


//	@Autowired
//	private IPendingApprovalService pendingApprovalService;

	@Autowired
	private PendingApprovalMapper pendingApprovalMapper;

	@Test
	void contextLoads() {
	}

//	@Test
//	public void testGetPendingApprovalList() {
//
//		List<PendingApproval> list = pendingApprovalService.list();
//		System.out.println(list);
//	}

	@Test
	public void testCallPro() {
		Map<String, String> hashMap = new HashMap<>();
		hashMap.put("UserCode", "zhengkg1");

		PendingApproval pendingApproval = pendingApprovalMapper.callProce(hashMap);

		System.out.println(pendingApproval);
	}

}

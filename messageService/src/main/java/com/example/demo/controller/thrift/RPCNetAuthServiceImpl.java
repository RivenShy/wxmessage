package com.example.demo.controller.thrift;

import com.example.demo.result.R;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Test on 2017/7/10.
 */
//@RestController("/thrift")
//@RequestMapping("/thrift")
public class RPCNetAuthServiceImpl implements RPCNetAuthService.Iface, RPCNetAuthService.IfaceTest {
    private static final Logger logger = LoggerFactory.getLogger(RPCNetAuthServiceImpl.class);



    @Override
    @RequestMapping("/login")
    public boolean login(@RequestParam("userAccount") String userAccount, @RequestParam("password") String password) throws TException {
        System.out.println("welcome to thrift, userAccount=" + userAccount + ", password=" +  password);
//        return R.data(userAccount);
        return true;
    }

    @Override
    @GetMapping("/loginTest")
    public R loginTest(String userAccount, String password) throws TException {
        System.out.println("loginTest, userAccount=" + userAccount + ", password=" +  password);
        return R.data(userAccount);
    }
}
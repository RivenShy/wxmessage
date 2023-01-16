package com.example.demo.grpc;

import com.example.demo.grpc.protocol.User;
import com.example.demo.grpc.protocol.UserServiceGrpc;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/grpc")
public class TestController {

    @GrpcClient("userClient")
    private UserServiceGrpc.UserServiceBlockingStub userService;

    @Autowired
    private GrpcInterceptor grpcInterceptor;

    @GetMapping("/getUser")
    public String getUser()     {
        User user = User.newBuilder()
                .setUserId(100)
                .putHobbys("pingpong", "play pingpong")
                .setCode(200)
                .build();

        Channel channel = ClientInterceptors.intercept(userService.getChannel(), grpcInterceptor);
        userService = UserServiceGrpc.newBlockingStub(channel);
        User u = userService.getUser(user);
        return u.getName();
    }
}

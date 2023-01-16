package com.example.demo.controller.thrift;

import com.example.demo.result.R;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

/**
 * Created by Test on 2017/7/10.
 */
public class AuthClient {

    private  RPCNetAuthService.Client rpcNetAuthService;
    private TBinaryProtocol protocol;
    private TSocket transport ;
//    public RPCAuthService.Client getRpcAuthService() {
//        return rpcAuthService;
//    }

    public RPCNetAuthService.Client getRpcNetAuthService() {
        return rpcNetAuthService;
    }

    public  void  open() throws TTransportException {
        transport.open();
    }
    public  void  close()
    {
        transport.close();
    }
    public AuthClient() throws TTransportException {
        transport = new TSocket("localhost",9090);
        protocol = new TBinaryProtocol(transport);

        TMultiplexedProtocol mp2 = new TMultiplexedProtocol(protocol, RPCNetAuthService.class.getSimpleName());
        rpcNetAuthService = new RPCNetAuthService.Client(mp2);
    }

    public static void main(String[] args) throws TException {
        AuthClient client = new  AuthClient();
        client.open();
        boolean loginResult = client.getRpcNetAuthService().login("zhangsan","123456");
        System.out.println("loginResult:" + loginResult);
//        R r = client.getRpcNetAuthService().loginTest("zhangsan","123456");
//        System.out.println("r:" + r);
        client.close();
    }
}

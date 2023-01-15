package com.example.demo.controller.jsonrpc;

import com.example.demo.result.R;
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.springframework.stereotype.Service;

//@Service
//@AutoJsonRpcServiceImpl
public class RpcServiceImpl implements JsonrpcService{
    @Override
    public R test(String str) {
        return R.data("hello" + str);
    }
}

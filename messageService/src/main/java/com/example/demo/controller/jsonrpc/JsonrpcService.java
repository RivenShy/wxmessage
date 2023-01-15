package com.example.demo.controller.jsonrpc;

import com.example.demo.result.R;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

//@JsonRpcService(value = "jsonRpc")
public interface JsonrpcService {

    public R test(@JsonRpcParam(value="str") String str);
}

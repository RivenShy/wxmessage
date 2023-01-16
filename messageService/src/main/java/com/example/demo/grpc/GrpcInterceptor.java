package com.example.demo.grpc;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//@Slf4j
//@Component
public class GrpcInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new CustomForwardingClientCall<>(next.newCall(method, callOptions));
    }
}

@Slf4j
class CustomCallListener<RespT> extends ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT> {

    protected CustomCallListener(ClientCall.Listener<RespT> delegate) {
        super(delegate);
    }
}

@Slf4j
class CustomForwardingClientCall<ReqT, RespT> extends ClientInterceptors.CheckedForwardingClientCall<ReqT, RespT> {

    protected CustomForwardingClientCall(ClientCall<ReqT, RespT> delegate) {
        super(delegate);
    }

    @Override
    protected void checkedStart(Listener<RespT> responseListener, Metadata headers) throws Exception {
        CustomCallListener<RespT> listener = new CustomCallListener<>(responseListener);
        delegate().start(listener, headers);
    }
}
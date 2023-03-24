package com.ceit.desktop.grpc.client;

import com.ceit.desktop.grpc.grpcjava.WebGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class WebToControlClient {
    //web通过grpc请求管控中心
    public WebGrpc.WebBlockingStub blockingStub;

    public WebToControlClient() {
        String ip = System.getProperty("web.grpc.ip");
        int port = Integer.valueOf(System.getProperty("web.grpc.port"));
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(ip,port)
                .usePlaintext()
                .build();
        blockingStub = WebGrpc.newBlockingStub(channel);
    }
}

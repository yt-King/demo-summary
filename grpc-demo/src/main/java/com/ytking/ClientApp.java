package com.ytking;

import com.ytking.proto.greeter.GreeterGrpc;
import com.ytking.proto.greeter.HelloReply;
import com.ytking.proto.greeter.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author yt
 * @package: com.ytking
 * @className: ClientApp
 * @date 2023/7/27
 * @description: TODO
 */
public class ClientApp {
    //远程连接管理器,管理连接的生命周期
    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public ClientApp(String host, int port) {
        //初始化连接
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        //初始化远程服务Stub
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        //关闭连接
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public String sayHello(String name) {
        //构造服务调用参数对象
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        //调用远程服务方法
        HelloReply response = blockingStub.sayHello(request);
        //返回值
        return response.getMessage();
    }

    public static void main(String[] args) throws InterruptedException {
        ClientApp client = new ClientApp("127.0.0.1", 50051);
        //服务调用
        String content = client.sayHello("Java client");
        //打印调用结果
        System.out.println(content);
        //关闭连接
        client.shutdown();
    }
}

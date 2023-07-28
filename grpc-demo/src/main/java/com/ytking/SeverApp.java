package com.ytking;

import com.ytking.proto.greeter.GreeterGrpc;
import com.ytking.proto.greeter.HelloReply;
import com.ytking.proto.greeter.HelloReplyOrBuilder;
import com.ytking.proto.greeter.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class SeverApp {
    Server server;

    private void start() throws IOException {
        int port = 50051;
        //这里可以添加多个模块
        server = ServerBuilder.forPort(port)
                .addService(new GreeterIml())  //这里可以添加多个模块
                .build()
                .start();
        System.out.println("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    SeverApp.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private static class GreeterIml extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply helloReply = HelloReply.newBuilder().setMessage("hello word").build();
            responseObserver.onNext(helloReply);
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final SeverApp server = new SeverApp();
        server.start();
        server.blockUntilShutdown();
    }
}

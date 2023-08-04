package com.ytking;

import io.prometheus.client.Counter;
import io.prometheus.client.exporter.PushGateway;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws InterruptedException, IOException {
        Random rnd = new Random();
        PushGateway gateway = new PushGateway("localhost:9091");
        Counter counterSuccess = Counter.build()
                .name("success_count") //请求访问成功数
                .labelNames("method_name") //请求名
                .help("method_name_success_count") //这个名字随便起
                .register(); //注：通常只能注册1次，1个实例中重复注册会报错
        Counter counterCostSuccess = Counter.build()
                .name("success_cost") //请求访问成功耗时
                .labelNames("method_name") //请求名
                .help("method_name_success_cost_count") //这个名字随便起
                .register(); //注：通常只能注册1次，1个实例中重复注册会报错
        while (true) {
            //请求成功数+随机数
            int num1 = rnd.nextInt(10);
            counterSuccess.labels("/item/user/v1/list").inc(num1);
            //请求成功耗时+随机数
            int num2 = rnd.nextInt(1000);
            counterCostSuccess.labels("/item/user/v1/list").inc(num2);
            counterCostSuccess.labels("/item/user/v1/query").inc(num2 + 100);
            //利用网关采集数据
            gateway.push(counterSuccess, "method_count", Map.of("client", "count"));
            gateway.push(counterCostSuccess, "method_count", Map.of("client", "cost"));
            System.out.println("counterSuccess:" + num1 + "       counterCostSuccess" + num2);
//            counterSuccess.remove("/item/user/v1/list");
//            counterCostSuccess.remove("/item/user/v1/list");
//            counterCostSuccess.remove("/item/user/v1/query");
            //辅助输出日志
            Thread.sleep(10000);
        }
    }
}

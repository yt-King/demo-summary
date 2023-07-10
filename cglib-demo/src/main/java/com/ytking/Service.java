package com.ytking;

/**
 * @author yt
 * @package: com.ytking
 * @className: Service
 * @date 2023/7/10
 * @description: 被代理类（目标类）
 */
public class Service {

    /**
     *  final 方法不能被子类覆盖
     */
    public final void finalMethod() {
        System.out.println("Service.finalMethod 执行了");
    }

    public void publicMethod() {
        System.out.println("Service.publicMethod 执行了");
    }
}
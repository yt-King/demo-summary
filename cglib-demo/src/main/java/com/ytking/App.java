package com.ytking;

import net.sf.cglib.core.DebuggingClassWriter;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        // 代理类class文件存入本地磁盘，可反编译查看源码
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./");
        // 1. 构造目标对象
        Service target = new Service();

        // 2. 根据目标对象生成代理对象
        CglibDynamicProxy proxy = new CglibDynamicProxy(target);

        // 获取 CGLIB 代理类
        Service proxyObject = proxy.getProxy();

        // 调用代理对象的方法
        proxyObject.finalMethod();
        proxyObject.publicMethod();
    }
}

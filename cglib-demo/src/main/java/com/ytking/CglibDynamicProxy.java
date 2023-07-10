package com.ytking;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author yt
 * @package: com.ytking
 * @className: CglibDynamicProxy
 * @date 2023/7/10
 * @description: 代理类
 */
public class CglibDynamicProxy implements MethodInterceptor {

    /**
     * 目标对象（也被称为被代理对象）
     */
    private Object target;

    public CglibDynamicProxy(Object target) {
        this.target = target;
    }

    /**
     * 如何增强
     *
     * @param obj    代理对象引用
     * @param method 被代理对象的方法的描述引用
     * @param args   方法参数
     * @param proxy  代理对象 对目标对象的方法的描述
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("CglibDynamicProxy intercept 方法执行前-------------------------------");

        System.out.println("obj = " + obj.getClass());
        System.out.println("method = " + method);
        System.out.println("proxy = " + proxy);

        Object object = proxy.invoke(target, args);
        System.out.println("CglibDynamicProxy intercept 方法执行后-------------------------------");
        return object;
    }

    /**
     * 获取被代理接口实例对象
     * <p>
     * 通过 enhancer.create 可以获得一个代理对象，它继承了 target.getClass() 类
     *
     * @param <T>
     * @return
     */
    public <T> T getProxy() {
        Enhancer enhancer = new Enhancer();
        //设置被代理类
        enhancer.setSuperclass(target.getClass());
        // 设置回调
        enhancer.setCallback(this);
        // create方法正式创建代理类
        return (T) enhancer.create();
    }
}

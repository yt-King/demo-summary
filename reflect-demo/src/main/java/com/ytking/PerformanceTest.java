package com.ytking;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yt
 * @package: com.ytking
 * @className: PerformanceTest
 * @date 2023/7/10
 * @description: 性能测试，通过反射与new比较实例化对象所需时间
 */
@Slf4j
public class PerformanceTest {

    private static final String TAG = "MainAc";
    private int MAX_TIMES = 100;
    private TestClass innerList[];

    protected void onCreate() {
        List<Long> newList = new ArrayList<>();
        List<Long> reflectList = new ArrayList<>();
        for (int k = 0; k < 6; k++) {
            innerList = new TestClass[MAX_TIMES];
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < MAX_TIMES; i++) {
                innerList[i] = new TestClass("jack", "hello word", 12, 1L);
            }
            newList.add((System.currentTimeMillis() - startTime));

            long startTime2 = System.currentTimeMillis();
            for (int i = 0; i < MAX_TIMES; i++) {
                innerList[i] = newInstanceByReflection();
            }
            reflectList.add((System.currentTimeMillis() - startTime2));
            MAX_TIMES = MAX_TIMES * 10;
        }
        log.info("次数: 一百次/一千次/一万次/十万次/一百万次/一千万次");
        log.info("new 创建耗时：" + newList);
        log.info("反射 创建耗时" + reflectList);
    }

    protected void onInvoke() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        List<Long> newList = new ArrayList<>();
        List<Long> reflectList = new ArrayList<>();
        MAX_TIMES = 10;
        for (int k = 0; k < 4; k++) {
            long startTime = System.currentTimeMillis();
            TestClass testClass = new TestClass("jack", "hello word", 12, 1L);
            for (int i = 0; i < MAX_TIMES; i++) {
                testClass.func();
            }
            newList.add((System.currentTimeMillis() - startTime));

            long startTime2 = System.currentTimeMillis();
            Class<?> clazz = Class.forName("com.ytking.TestClass");
            TestClass obj = (TestClass) clazz.getDeclaredConstructor(String.class, String.class, Integer.class, Long.class).newInstance("jack", "hello word", 12, 1L);
            Method func = clazz.getMethod("func");
            for (int i = 0; i < MAX_TIMES; i++) {
                func.invoke(obj);
            }
            reflectList.add((System.currentTimeMillis() - startTime2));
            MAX_TIMES = MAX_TIMES * 10;
        }
        log.info("次数: 十次/一百次/一千次/一万次");
        log.info("普通调用耗时：" + newList);//[126, 1221, 12112, 121579]
        log.info("反射调用耗时：" + reflectList);//[114, 1238, 12107, 120732]
    }

    public TestClass newInstanceByReflection() {
        Class<TestClass> clazz = TestClass.class;
        try {
            return (TestClass) clazz.getDeclaredConstructor(String.class, String.class, Integer.class, Long.class).newInstance("jack", "hello word", 12, 1L);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}

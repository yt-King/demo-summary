package com.ytking;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
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
    private final int MAX_TIMES = 1000000;
    private InnerClass innerList[];

    protected void onCreate() {

        innerList = new InnerClass[MAX_TIMES];

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < MAX_TIMES; i++) {
            innerList[i] = new InnerClass();
        }
        log.info(TAG + "totalTime: " + (System.currentTimeMillis() - startTime));

        long startTime2 = System.currentTimeMillis();
        for (int i = 0; i < MAX_TIMES; i++) {
            innerList[i] = newInstanceByReflection();
        }
        log.info(TAG + "totalTime2: " + (System.currentTimeMillis() - startTime2));
    }

    public InnerClass newInstanceByReflection() {
        Class<InnerClass> clazz = InnerClass.class;
        try {
            return (InnerClass) clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class InnerClass {
        private static final String attribute1 = "attribute1";
        private static final String attribute2 = "attribute2";

        public static void func() {
            List<Object> list = new ArrayList<>();
        }
    }
}

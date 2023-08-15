package com.ytking;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yt
 * @package: com.ytking
 * @className: TestClass
 * @date 2023/8/8
 * @description: TODO
 */
public class TestClass {
    String name;
    String value;
    Integer age;
    Long speed;
    private static final String attribute1 = "attribute1";
    private static final String attribute2 = "attribute2";

    public TestClass(String name, String value, Integer age, Long speed) {
        this.name = name;
        this.value = value;
        this.age = age;
        this.speed = speed;
    }

    public void func() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

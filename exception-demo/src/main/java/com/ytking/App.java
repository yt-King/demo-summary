package com.ytking;

import lombok.extern.slf4j.Slf4j;

/**
 * <a href="https://www.cnblogs.com/javastack/p/13596096.html">示例</a>
 */
@Slf4j
public class App {
    public static Class<?> classFind(ClassFindInterface classFindInterface, String className) {
        Class<?> find = null;
        try {
            find = classFindInterface.findClass(className);
        } catch (Exception e) {
            log.info("find error:{}", (Object) e.getStackTrace());
        }
        return find;
    }

    public static void main(String[] args) {
        Class<?> aClass = classFind(Class::forName, "lombok.extern.slf4j.Slf4");
        System.out.println(aClass);
    }
}

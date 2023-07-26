package com.ytking;

/**
 * @author yt
 * @package: com.ytking
 * @className: ClassFindInterface
 * @date 2023/7/26
 * @description: 根据类名查找类的接口函数
 */
@FunctionalInterface
public interface ClassFindInterface {
    Class<?> findClass(String name) throws Exception;
}

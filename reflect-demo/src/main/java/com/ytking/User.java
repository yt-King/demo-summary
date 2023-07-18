package com.ytking;

/**
 * @author yt
 * @package: com.ytking
 * @className: User
 * @date 2023/7/13
 * @description: 测试实体类
 */
public class User {
    public String name = "init";
    private int age;

    public User() {
    }

    public User(String name) {
        super();
        this.name = name;
    }

    private User(int age, String name) {
        super();
        this.name = name;
        this.age = age;
    }

    private User(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }

    private String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", age=" + age + "]";
    }
}

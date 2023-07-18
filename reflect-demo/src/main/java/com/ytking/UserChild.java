package com.ytking;

/**
 * @author yt
 * @package: com.ytking
 * @className: UserChiled
 * @date 2023/7/18
 * @description: TODO
 */
public class UserChild extends User {
    public int childAge;
    private String childName;

    public UserChild() {
        super();
    }

    public void say(String word) {
        System.out.println("hello" + word);
    }

    public void hello() {
        System.out.println("hello");
    }

    @Override
    public String toString() {
        return "UserChild [name=" + name + ",childName=" + childName + " childAge=" + childAge + "]";
    }
}

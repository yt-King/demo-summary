package com.ytking;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Hello world!
 */
@Slf4j
public class App {
    public static void main(String[] args) throws Exception {
        log.info("---------------性能测试-----------------");
        //性能测试
        PerformanceTest performanceTest = new PerformanceTest();
        performanceTest.onCreate();
        performanceTest.onInvoke();
//        //类测试
//        classTest();
//        //构造器测试
//        constructTest();
//        //字段测试
//        fieldTest();
//        // 方法测试
//        methodTest();
    }

    private static void classTest() throws Exception {
        log.info("--------------class测试----------------");
        // 获取Class对象的三种方式
        log.info("根据类名:  \t" + User.class);
        log.info("根据对象:  \t" + new User().getClass());
        log.info("根据全限定类名:\t" + Class.forName("com.ytking.User"));
        // 常用的方法
        Class<User> userClass = User.class;
        log.info("获取全限定类名:\t" + userClass.getName());
        log.info("获取类名:\t" + userClass.getSimpleName());
        log.info("实例化:\t" + userClass.newInstance());
    }

    private static void constructTest() throws Exception {
        log.info("--------------construct测试----------------");
        //获取Class对象的引用
        Class<?> clazz = Class.forName("com.ytking.User");

        log.info("实例化默认构造方法,User必须无参构造函数,否则将抛异常");
        User user = (User) clazz.newInstance();
        user.setAge(20);
        user.setName("Jack");
        System.out.println(user);
        log.info("获取带String参数的public构造函数");
        Constructor<?> cs1 = clazz.getConstructor(String.class);
        //创建User
        User user1 = (User) cs1.newInstance("hiway");
        user1.setAge(22);
        System.out.println("user1:" + user1);
        log.info("取得指定带int和String参数构造函数,该方法是私有构造private");
        Constructor cs2 = clazz.getDeclaredConstructor(int.class, String.class);
        Constructor cs3 = clazz.getDeclaredConstructor(String.class, int.class);
        //由于是private必须设置可访问
        cs2.setAccessible(true);
        cs3.setAccessible(true);
        //创建user对象
        User user2 = (User) cs2.newInstance(25, "hiway2");
        User user3 = (User) cs3.newInstance("25", 25);
        System.out.println("user2:" + user2);
        System.out.println("user2:" + user3);
        //获取所有构造包含private
        Constructor<?> cons[] = clazz.getDeclaredConstructors();
        log.info("查看每个构造方法需要的参数");
        for (int i = 0; i < cons.length; i++) {
            //获取构造函数参数类型
            Class<?> clazzs[] = cons[i].getParameterTypes();
            System.out.println("构造函数[" + i + "]:" + cons[i].toString());
            System.out.print("参数类型[" + i + "]:(");
            for (int j = 0; j < clazzs.length; j++) {
                if (j == clazzs.length - 1)
                    System.out.print(clazzs[j].getName());
                else
                    System.out.print(clazzs[j].getName() + ",");
            }
            System.out.println(")");
        }
    }

    private static void fieldTest() throws Exception {
        log.info("--------------field测试----------------");
        Class<?> clazz = Class.forName("com.ytking.UserChild");
        //获取指定字段名称的Field类,注意字段修饰符必须为public而且存在该字段,
        //否则抛NoSuchFieldException
        Field field = clazz.getField("childAge");
        System.out.println("field:" + field);

        //获取所有修饰符为public的字段,包含父类字段,注意修饰符为public才会获取
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            System.out.println("f1:" + f.getDeclaringClass() + ":" + f.getName());
        }

        //获取当前类所字段(包含private字段),注意不包含父类的字段
        Field[] fields2 = clazz.getDeclaredFields();
        for (Field f : fields2) {
            System.out.println("f2:" + f.getDeclaringClass() + ":" + f.getName());
        }

        //获取指定字段名称的Field类,可以是任意修饰符的字段,注意不包含父类的字段
        Field field2 = clazz.getDeclaredField("childName");
        System.out.println("field2:" + field2);

        //获取Class对象
        UserChild st = (UserChild) clazz.newInstance();
        //获取父类public字段并赋值
        Field ageField = clazz.getField("name");
        ageField.set(st, "haha");
        Field scoreField = clazz.getDeclaredField("childAge");
        //设置可访问，score是private的
        scoreField.setAccessible(true);
        scoreField.set(st, 88);
        System.out.println(st);
    }

    private static void methodTest() throws Exception {
        log.info("--------------method测试----------------");

        Class<?> clazz = Class.forName("com.ytking.UserChild");

        UserChild userChild = (UserChild) clazz.newInstance();

        //根据参数获取public的Method,包含继承自父类的方法
        Method method = clazz.getMethod("hello");

        System.out.println("method:" + method);

        //获取所有public的方法:
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            System.out.println("m::" + m);
        }

        //获取指定参数的方法对象Method
        Method method1 = clazz.getMethod("say", String.class);

        //通过Method对象的invoke(Object obj,Object... args)方法调用
        method1.invoke(userChild, "word");
    }
}

package com.ytking;

import com.github.benmanes.caffeine.cache.*;
import com.ytking.TinyLFU.CountMinSketch;
import com.ytking.TinyLFU.FrequencySketch;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * Hello world!
 */
@Slf4j
public class App {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        evictionBySize();
//        log.info("-----------------------------");
//        evictionByTime();
//        log.info("-----------------------------");
        evictionByConfig();
//        log.info("-----------------------------");
//        demo();
//        log.info("-----------------------------");
//        manualLoading();
//        log.info("-----------------------------");
//        autoLoading();
//        log.info("-----------------------------");
//        ascManualLoading();
//        log.info("-----------------------------");
//        ascAutoLoading();
//        log.info("-----------------------------");
//        evictionBySize();
//        log.info("-----------------------------");
//        tinyLFUTest();
//        log.info("-----------------------------");
//        frequenceTest();
//        log.info("-----------------------------");
//        refreshTest();
    }

    public static void demo() {
        Cache<String, String> cache = Caffeine.newBuilder()
                //最大个数限制
                //最大容量1024个，超过会自动清理空间
                .maximumSize(1024)
                //初始化容量
                .initialCapacity(1)
                //访问后过期（包括读和写）
                //5秒没有读写自动删除
                .expireAfterAccess(5, TimeUnit.SECONDS)
                //写后过期
                .expireAfterWrite(2, TimeUnit.HOURS)
                //写后自动异步刷新
                .refreshAfterWrite(1, TimeUnit.HOURS)
                //记录下缓存的一些统计数据，例如命中率等
                .recordStats()
                .removalListener(((key, value, cause) -> {
                    //清理通知 key,value ==> 键值对   cause ==> 清理原因
                    System.out.println("removed key=" + key);
                }))
                //使用CacheLoader创建一个LoadingCache
                .build(new CacheLoader<String, String>() {
                    //同步加载数据
                    @Nullable
                    @Override
                    public String load(@NonNull String key) throws Exception {
                        System.out.println("loading  key=" + key);
                        return "value_" + key;
                    }

                    //异步加载数据
                    @Nullable
                    @Override
                    public String reload(@NonNull String key, @NonNull String oldValue) throws Exception {
                        System.out.println("reloading  key=" + key);
                        return "value_" + key;
                    }
                });
        //添加值
        cache.put("name", "ytKing");
        cache.put("key", "NB!");

        //获取值
        String value = cache.getIfPresent("name");
        System.out.println("value = " + value);
        //remove
        cache.invalidate("name");
        value = cache.getIfPresent("name");
        System.out.println("value = " + value);
    }

    //手动加载
    public static void manualLoading() {
        Cache<String, Integer> cache = Caffeine.newBuilder().build();

        Integer age1 = cache.getIfPresent("张三");
        System.out.println(age1);

        //当key不存在时，会立即创建出对象来返回，age2不会为空
        Integer age2 = cache.get("张三", k -> {
            System.out.println("k:" + k);
            return 18;
        });
        System.out.println(age2);
    }

    //自动加载
    public static void autoLoading() {
        //此时的类型是 LoadingCache 不是 Cache
        LoadingCache<String, Integer> cache = Caffeine.newBuilder().build(key -> {
            System.out.println("自动填充:" + key);
            return 18;
        });

        Integer age1 = cache.getIfPresent("张三");
        System.out.println(age1);

        // key 不存在时 会根据给定的CacheLoader自动装载进去
        Integer age2 = cache.get("张三");
        System.out.println(age2);
    }

    //异步手动加载
    public static void ascManualLoading() throws ExecutionException, InterruptedException {
        AsyncCache<String, Integer> cache = Caffeine.newBuilder().buildAsync();

        //会返回一个 future对象， 调用future对象的get方法会一直卡住直到得到返回，和多线程的submit一样
        CompletableFuture<Integer> ageFuture = cache.get("张三", name -> {
            System.out.println("name:" + name);
            return 18;
        });

        Integer age = ageFuture.get();
        System.out.println("age:" + age);
    }

    //异步自动加载
    public static void ascAutoLoading() throws ExecutionException, InterruptedException {
        AsyncLoadingCache<String, Integer> cache = Caffeine.newBuilder().buildAsync(name -> {
            System.out.println("name:" + name);
            return 18;
        });
        CompletableFuture<Integer> ageFuture = cache.get("张三");

        Integer age = ageFuture.get();
        System.out.println("age:" + age);

    }

    //基于大小驱逐
    public static void evictionBySize() throws InterruptedException {
        // 创建一个 Caffeine 缓存，使用基于大小的驱逐策略
        Cache<String, String> cache = Caffeine.newBuilder()
                // 设置缓存的最大大小为 3
                .maximumSize(3)
                .removalListener((String key, String value, RemovalCause cause) ->
                        System.out.printf("Key %s was removed (%s)%n", key, cause))
                .build();

        // 添加数据到缓存
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        // 添加第四个数据，在下一次get时将触发基于大小的驱逐
        cache.put("key4", "value4");
        System.out.println("Data for key1: " + cache.getIfPresent("key1"));
        //睡眠100ms确保顺序输出
        Thread.sleep(100);
        System.out.println("Data for key1: " + cache.getIfPresent("key1"));
        log.info("-----------------------------");
        // 创建一个 Caffeine 缓存，使用基于权重的驱逐策略
        Cache<String, String> weightCache = Caffeine.newBuilder()
                // 设置缓存的最大权重为10
                .maximumWeight(10)
                // 定义权重函数
                .weigher((String key, String value) -> key.length() + value.length())
                .removalListener((String key, String value, RemovalCause cause) ->
                        System.out.printf("Key %s was removed (%s)%n", key, cause))
                .build();

        // 添加数据到缓存
        weightCache.put("key1", "value1");
        // 权重总和超过10以后下次get触发驱逐
        weightCache.put("key2", "value2");
        weightCache.put("key3", "value3");
        weightCache.put("key4", "value4");
        System.out.println("Data for key1: " + weightCache.getIfPresent("key1"));
        //睡眠100ms确保顺序输出
        Thread.sleep(100);
        System.out.println("Data for key4: " + weightCache.getIfPresent("key4"));
    }

    //基于时间驱逐
    public static void evictionByTime() throws InterruptedException {
        // 创建一个 Caffeine 缓存，在最后一次写入缓存后开始计时，在指定的时间后过期
        Cache<String, String> cacheAfterWrite = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .removalListener((String key, String value, RemovalCause cause) ->
                        System.out.printf("Key %s was removed (%s)%n", key, cause))
                .build();

        // 添加数据到缓存
        cacheAfterWrite.put("key1", "value1");
        // 睡眠1.5秒
        Thread.sleep(1500);
        System.out.println("Data for key1: " + cacheAfterWrite.getIfPresent("key1"));
        log.info("-----------------------------");
        // 创建一个 Caffeine 缓存，在最后一次访问或者写入后开始计时，在指定的时间后过期。假如一直有请求访问该key，那么这个缓存将一直不会过期。
        Cache<String, String> cacheAfterAccess = Caffeine.newBuilder()
                // 设置缓存的最大权重为10
                .expireAfterAccess(1, TimeUnit.SECONDS)
                .removalListener((String key, String value, RemovalCause cause) ->
                        System.out.printf("Key %s was removed (%s)%n", key, cause))
                .build();
        // 添加数据到缓存
        cacheAfterAccess.put("key1", "value1");
        Random random = new Random();
        while (cacheAfterAccess.getIfPresent("key1") != null) {
            int time = random.nextInt(1500);
            // 睡眠
            Thread.sleep(time);
            System.out.println("sleep " + time + " ms,and Data for key1: " + cacheAfterAccess.getIfPresent("key1"));
        }
    }

    //自定义驱逐
    public static void evictionByConfig() {
        // 创建一个Caffeine缓存，使用自定义的过期策略
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfter(new Expiry<String, String>() {
                    @Override
                    public long expireAfterCreate(String key, String value, long currentTime) {
                        // 返回缓存项的过期时间，单位为纳秒
                        // 在这个例子中，我们将过期时间设置为5秒钟
                        return TimeUnit.SECONDS.toNanos(5);
                    }

                    @Override
                    public long expireAfterUpdate(String key, String value, long currentTime, @NonNegative long currentDuration) {
                        // 返回缓存项的更新后的过期时间，单位为毫秒
                        return currentDuration; // 保持不变
                    }

                    @Override
                    public long expireAfterRead(String key, String value, long currentTime, @NonNegative long currentDuration) {
                        // 返回缓存项的更新后的过期时间，单位为毫秒
                        return currentDuration; // 保持不变
                    }
                })
                .build();
        // 将数据放入缓存
        cache.put("key", "value");
        // 从缓存中获取数据
        String result = cache.getIfPresent("key");
        System.out.println("Result: " + result);
    }

    public static void frequenceTest() {
        FrequencySketch<Character> stringFrequencySketch = new FrequencySketch<>();
        stringFrequencySketch.ensureCapacity(20);
        List<Character> keys = List.of('a', 'b', 'c', 'a', 'a', 'a', 'a', 'b', 'c', 'd');
        keys.forEach(stringFrequencySketch::increment);
        List<Character> res = List.of('a', 'b', 'c', 'd');
        res.forEach(stringFrequencySketch::frequency);
    }

    public static void tinyLFUTest() {
        //TinyLFU
        List<Character> keys1 = List.of('a', 'b', 'c', 'a', 'a', 'a', 'a', 'b', 'c', 'd');
        CountMinSketch tinyLFU = new CountMinSketch();
        keys1.forEach(x -> tinyLFU.update(String.valueOf(x)));
        List<Character> res = List.of('a', 'b', 'c', 'd');
        res.forEach(x -> System.out.println(tinyLFU.estimate(String.valueOf(x))));
    }

    public static void refreshTest() throws InterruptedException {
        Cache<String, String> cache = Caffeine.newBuilder().refreshAfterWrite(1, TimeUnit.SECONDS)
                .build(key -> "refreshed");
        cache.put("key", "value");
        //value
        System.out.println(cache.getIfPresent("key"));
        Thread.sleep(1500);
        //refreshed
        System.out.println(cache.getIfPresent("key"));
    }

}

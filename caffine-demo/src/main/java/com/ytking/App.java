package com.ytking;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ytking.TinyLFU.CountMinSketch;
import com.ytking.TinyLFU.FrequencySketch;

import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Cache<Object, Object> cache = Caffeine.newBuilder().maximumSize(100).build();
        cache.put("asd","asd");
        test();
        frequenceTest();
    }

    public static void frequenceTest() {
        FrequencySketch<Character> stringFrequencySketch = new FrequencySketch<>();
        stringFrequencySketch.ensureCapacity(20);
        List<Character> keys = List.of('a', 'b', 'c', 'a', 'a', 'a', 'a', 'b', 'c', 'd');
        keys.forEach(stringFrequencySketch::increment);
        List<Character> res = List.of('a', 'b', 'c', 'd');
        res.forEach(stringFrequencySketch::frequency);
    }

    public static void test() {
        //TinyLFU
        List<Character> keys1 = List.of('a', 'b', 'c', 'a', 'a', 'a', 'a', 'b', 'c', 'd');
        CountMinSketch tinyLFU = new CountMinSketch();
        keys1.forEach(x -> tinyLFU.update(String.valueOf(x)));
        List<Character> res = List.of('a', 'b', 'c', 'd');
        res.forEach(x -> System.out.println(tinyLFU.estimate(String.valueOf(x))));
    }
}

package com.ytking;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ytking.LRU.LruSegment;
import com.ytking.TinyLFU.FrequencySketch;
import com.ytking.TinyLFU.TinyLFU;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
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
        //LRU
        List<Character> keys = List.of('a', 'b', 'c', 'a', 'a', 'a', 'a', 'b', 'c', 'd');
        LruSegment<String, String> lruSegment = new LruSegment<>(3);
        keys.forEach(x -> lruSegment.put(String.valueOf(x), ""));
        lruSegment.getAllk().forEach(System.out::println);//b、c、d
        //TinyLFU
        List<Character> keys1 = List.of('a', 'b', 'c', 'a', 'a', 'a', 'a', 'b', 'c', 'd');
        TinyLFU tinyLFU = new TinyLFU();
        keys1.forEach(x -> tinyLFU.update(String.valueOf(x)));
        List<Character> res = List.of('a', 'b', 'c', 'd');
        res.forEach(x -> System.out.println(tinyLFU.estimate(String.valueOf(x))));
    }
}

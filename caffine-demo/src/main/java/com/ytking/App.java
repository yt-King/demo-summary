package com.ytking;

import com.ytking.TinyLFU.FrequencySketch;
import com.ytking.TinyLFU.TinyLFU;

import java.util.List;

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
        //TinyLFU
        List<Character> keys1 = List.of('a', 'b', 'c', 'a', 'a', 'a', 'a', 'b', 'c', 'd');
        TinyLFU tinyLFU = new TinyLFU();
        keys1.forEach(x -> tinyLFU.update(String.valueOf(x)));
        List<Character> res = List.of('a', 'b', 'c', 'd');
        res.forEach(x -> System.out.println(tinyLFU.estimate(String.valueOf(x))));
    }
}

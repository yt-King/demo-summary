package com.ytking.LFU;

import lombok.experimental.Helper;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author yt
 * @package: com.ytking.LFU
 * @className: LFUCache
 * @date 2023/9/21
 * @description: TODO
 */

public class LFUCache<K, V> {
    private final int capacity;
    private final Map<K, V> cache;
    private final Map<K, Integer> frequency;
    private final Map<Integer, LinkedHashSet<K>> frequencyLists;
    private int minFrequency;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.frequency = new HashMap<>();
        this.frequencyLists = new HashMap<>();
        this.minFrequency = 0;
    }

    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        // 更新频率
        int freq = frequency.get(key);
        frequency.put(key, freq + 1);
        // 移动到下一个频率列表
        frequencyLists.get(freq).remove(key);
        if (freq == minFrequency && frequencyLists.get(freq).isEmpty()) {
            minFrequency++;
        }
        frequencyLists.computeIfAbsent(freq + 1, k -> new LinkedHashSet<>()).add(key);
        return cache.get(key);
    }

    public void put(K key, V value) {
        if (capacity <= 0) {
            return;
        }
        if (cache.size() >= capacity) {
            // 移除最低频率的最久未使用的数据项
            K evicted = frequencyLists.get(minFrequency).iterator().next();
            frequencyLists.get(minFrequency).remove(evicted);
            cache.remove(evicted);
            frequency.remove(evicted);
        }
        // 添加新数据项
        cache.put(key, value);
        frequency.put(key, 1);
        frequencyLists.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        minFrequency = 1;
    }

    public static void main(String[] args) {
        LFUCache<Integer, String> cache = new LFUCache<>(2);

        cache.put(1, "One");
        cache.put(2, "Two");
        System.out.println(cache.get(1)); // 输出：One
        cache.put(3, "Three"); // 移除最低频率的数据项2
        System.out.println(cache.get(2)); // 输出：null，因为2已被移除
    }
}

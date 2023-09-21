package com.ytking.LRU;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

    public static void main(String[] args) {
        LRUCache<Integer, String> lruCache = new LRUCache<>(3);

        lruCache.put(1, "One");
        lruCache.put(2, "Two");
        lruCache.put(3, "Three");

        System.out.println(lruCache); // 输出：{1=One, 2=Two, 3=Three}

        lruCache.get(1); // 从缓存中获取键为1的数据项，将它提升为最近使用的
        lruCache.put(4, "Four"); // 添加新的数据项，导致缓存满，需要淘汰最久未使用的数据项

        System.out.println(lruCache); // 输出：{3=Three, 1=One, 4=Four}
    }
}

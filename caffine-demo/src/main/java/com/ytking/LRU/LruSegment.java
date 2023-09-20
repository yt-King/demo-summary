package com.ytking.LRU;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author yt
 * @package: com.ytking.LRU
 * @className: LruSegment
 * @date 2023/9/13
 * @description: TODO
 */
public class LruSegment<K, V> {
    private int capacity;

    private Map<K, Node<K, V>> keyArrays;
    private Node<K, V> head, tail;

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();

    public LruSegment(int capacity) {
        this.capacity = capacity;
        keyArrays = new HashMap<>();
    }

    public V put(K key, V value) {
        Node<K, V> remove = null;
        writeLock.lock();
        try {
            if (keyArrays.containsKey(key)) {
                Node<K, V> kvNode = keyArrays.get(key);
                kvNode.value = value;
                remove(kvNode);
                add(kvNode);
            } else {
                if (keyArrays.size() >= capacity) {
                    remove = head;
                    keyArrays.remove(head.key);
                    remove(head);
                }
                Node<K, V> kvNode = new Node<>(key, value);
                add(kvNode);
                keyArrays.put(key, kvNode);
            }
        } finally {
            writeLock.unlock();
        }
        return remove == null ? null : remove.value;
    }

    public V get(K key) {
        writeLock.lock();
        try {
            Node<K, V> node = keyArrays.get(key);
            if (node == null) return null;
            remove(node);
            add(node);
            return node.value;
        } finally {
            writeLock.unlock();
        }
    }

    public int size() {
        readLock.lock();
        try {
            return keyArrays.size();
        } finally {
            readLock.unlock();
        }
    }

    public Set<K> getAllk() {
        readLock.lock();
        try {
            HashSet<K> vs = new HashSet<>();
            Node<K, V> node = tail;
            while (node != null) {
                vs.add(node.key);
                node = node.pre;
            }
            return vs;
        } finally {
            readLock.unlock();
        }
    }

    private void add(Node<K, V> node) {
        if (node == null) {
            return;
        }

        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.pre = tail;
            node.next = null;
            tail = node;
        }
    }

    private void remove(Node<K, V> node) {
        if (node == null) {
            return;
        }

        if (node.pre == null) {
            head = node.next;
        } else {
            node.pre.next = node.next;
        }

        if (node.next != null) {
            node.next.pre = node.pre;
        } else {
            tail = node.pre;
        }
    }
}


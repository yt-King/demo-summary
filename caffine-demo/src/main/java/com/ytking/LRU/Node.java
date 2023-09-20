package com.ytking.LRU;

/**
 * @author yt
 * @package: com.ytking.LRU
 * @className: Node
 * @date 2023/9/13
 * @description: 选择链表来记录数据被访问的先后顺序
 */
public class Node<K, V> {
    K key;
    V value;

    Node<K, V> pre, next;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}


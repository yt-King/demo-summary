package com.ytking.FIFO;

import java.util.LinkedList;
import java.util.Queue;

public class FIFOQueue<T> {
    private final Queue<T> queue;
    private final int maxSize;

    public FIFOQueue(int maxSize) {
        this.queue = new LinkedList<>();
        this.maxSize = maxSize;
    }

    // 添加元素到队列尾部
    public void enqueue(T item) {
        if (queue.size() >= maxSize) {
            // 如果队列已满，移除队列头部的元素
            queue.poll();
        }
        queue.offer(item);
    }

    // 从队列头部移除并返回元素
    public T dequeue() {
        return queue.poll();
    }

    // 获取队列的大小
    public int size() {
        return queue.size();
    }

    // 检查队列是否为空
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // 清空队列
    public void clear() {
        queue.clear();
    }

    public static void main(String[] args) {
        FIFOQueue<Integer> fifoQueue = new FIFOQueue<>(5);

        // 向队列中添加元素
        for (int i = 1; i <= 5; i++) {
            fifoQueue.enqueue(i);
        }

        // 输出队列元素
        System.out.println("队列元素：");
        while (!fifoQueue.isEmpty()) {
            System.out.println(fifoQueue.dequeue());
        }
    }
}
package com.ytking.TinyLFU;

import java.util.BitSet;
import java.util.Random;

/**
 * @author yt
 * @package: com.ytking.TinyLFU
 * @className: TinyLFU
 * @date 2023/9/13
 * @description: TODO
 */
public class TinyLFU {
    //Count-Min Sketch的宽度，也就是哈希表的列数。它决定了用于存储计数的空间大小
    private int width;
    //Count-Min Sketch的深度，也就是哈希函数的数量。深度决定了哈希函数的数量，每个哈希函数生成一个不同的哈希位置，用于更新和查询计数。
    private int depth;
    //DoorKeeper机制的阈值。仅当估计的计数超过此阈值时，元素才会被存储在Count-Min Sketch中
    private int threshold;
    // 使用long数组存储计数器，每个long存储16个计数器
    private long[][] sketch;
    //一个数组，包含深度个哈希函数的随机种子，用于生成哈希位置
    private int[] hashFunctions;
    //一个数组，用于跟踪哪些哈希位置包含了元素，DoorKeeper机制使用它来决定是否存储元素
    private BitSet doorKeeper;
    //保鲜机制触发阈值
    private int totalCount;

    public TinyLFU() {
        this(16, 4, 1, 100);
    }

    public TinyLFU(int width, int depth, int threshold, int totalCount) {
        this.width = width;
        this.depth = depth;
        this.threshold = threshold;
        // 使用long数组存储计数器，每个long存储16个计数器
        // 在Caffeine的实现中，会先创建一个Long类型的数组，数组的大小为 2的幂次大小。
        // Caffeine将64位的Long类型划分为4段，每段16位，用于存储4种hash算法对应的数据访问频率计数。
        this.sketch = new long[depth][width / 16];
        this.doorKeeper = new BitSet(width);
        this.hashFunctions = new int[depth];
        this.totalCount = totalCount;

        // Initialize hash functions
        Random rand = new Random();
        for (int i = 0; i < depth; i++) {
            hashFunctions[i] = rand.nextInt(Integer.MAX_VALUE);
        }
    }

    //添加数据默认count为1
    public void update(String key) {
        this.update(key, 1);
    }

    public void update(String key, int count) {
        //保鲜机制
        if (totalCount > 100) {
            divideCountersByTwo();
            totalCount = 0;
        }
        totalCount++;
        // 如果元素在DoorKeeper中，直接插入Count-Min Sketch的主结构
        if (doorKeeper.get(hash(key))) {
            for (int i = 0; i < depth; i++) {
                int hash = hash(key, i);
                int slot = hash / 16; // 每四位一个计数器，一个Long64位，共16个计数器
                int offset = hash % 16; // 计数器内的偏移量

                // 限制计数为 15
                long currentCounter = sketch[i][slot] >> (offset * 4) & 0xF;
                long updatedCounter = Math.min(15, currentCounter + count);

                // 更新计数器，先将指定位置的数据都置0，再进行赋值
                sketch[i][slot] &= ~(0xFL << (offset * 4));
                sketch[i][slot] |= (updatedCounter << (offset * 4));
            }
        } else {
            // 否则，将元素标记为 DoorKeeper 中
            doorKeeper.set(hash(key));
        }
    }

    public int estimate(String key) {
        int minCount = Integer.MAX_VALUE;
        for (int i = 0; i < depth; i++) {
            int hash = hash(key, i);
            int slot = hash / 16;
            int offset = hash % 16;
            long counter = sketch[i][slot] >> (offset * 4) & 0xF;
            minCount = Math.min(minCount, (int) counter);
        }
        // 如果元素在 DoorKeeper 中，返回计数值，同时加上 DoorKeeper 中的计数值
        if (doorKeeper.get(hash(key))) {
            minCount += 1;
        }

        return minCount;
    }

    //保鲜机制
    public void divideCountersByTwo() {
        doorKeeper.clear();
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < sketch[i].length; j++) {
                long currentLong = sketch[i][j];
                for (int k = 0; k < 16; k++) {
                    int offset = k * 4;
                    int currentCounter = (int) ((currentLong >> offset) & 0xF);
                    int newCounter = (currentCounter >> 1) & 0xF; // 使用位运算除以2
                    currentLong &= ~(0xFL << offset);
                    currentLong |= (long) newCounter << offset;
                }
                sketch[i][j] = currentLong;
            }
        }
    }

    private int hash(String key) {
        return this.hash(key, 0);
    }

    private int hash(String key, int hashFunctionIndex) {
        long hash = hashFunctions[hashFunctionIndex];
        for (char c : key.toCharArray()) {
            hash = (hash * 31 + c) % width;
        }
        return (int) hash;
    }

}

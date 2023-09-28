package com.ytking.TinyLFU;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * @author yt
 * @package: com.ytking.TinyLFU
 * @className: FrequencySketch
 * @date 2023/9/20
 * @description: TODO
 */
@Slf4j
public class FrequencySketch<E> {
    static final long RESET_MASK = 0x7777777777777777L;
    static final long ONE_MASK = 0x1111111111111111L;

    int sampleSize;
    int blockMask;
    long[] table;
    int size;

    public FrequencySketch() {
    }

    /**
     * 初始化实例的容量，大小为大于等于给定数的最小二次幂，
     * 以确保它可以在给定缓存最大大小的情况下准确估计元素的频率。
     * 调整大小时此操作会忘记所有先前的计数。
     *
     * @param maximumSize 缓存容量的最大值
     */
    public void ensureCapacity(@NonNegative long maximumSize) {
        int maximum = (int) Math.min(maximumSize, Integer.MAX_VALUE >>> 1);
        if ((table != null) && (table.length >= maximum)) {
            return;
        }

        table = new long[Math.max(ceilingPowerOfTwo(maximum), 8)];
        sampleSize = (maximumSize == 0) ? 10 : (10 * maximum);
        //右移三位有助于将哈希码的高位和低位分散到不同的块中。这可以减少哈希冲突，提高元素在不同块中的分布性，从而更准确地估计元素的频率。
        blockMask = (table.length >>> 3) - 1;
        if (sampleSize <= 0) {
            sampleSize = Integer.MAX_VALUE;
        }
        size = 0;
    }

    public boolean isNotInitialized() {
        return (table == null);
    }

    /**
     * 返回元素出现的估计次数，最多可达最大值 (15)。
     *
     */
    @NonNegative
    public int frequency(E e) {
        if (isNotInitialized()) {
            return 0;
        }

        int[] count = new int[4];
        int blockHash = spread(e.hashCode());
        int counterHash = rehash(blockHash);
        int block = (blockHash & blockMask) << 3;
        for (int i = 0; i < 4; i++) {
            int h = counterHash >>> (i << 3);
            int index = (h >>> 1) & 15;
            int offset = h & 1;
            count[i] = (int) ((table[block + offset + (i << 1)] >>> (index << 2)) & 0xfL);
        }
        int min = Math.min(Math.min(count[0], count[1]), Math.min(count[2], count[3]));
        log.info("frequency-{}:{}", e, min);
        return min;
    }

    /**
     * 如果元素的受欢迎程度不超过最大值 (15)，则增加该元素的出现次数。
     * 当观察到的总记录次数超过阈值时，所有元素的流行度将定期进行下采样。此过程提供了频率老化，以允许过期的长期条目逐渐消失。
     *
     * @param e 需要添加的元素
     */
    public void increment(E e) {
        if (isNotInitialized()) {
            return;
        }

        int[] index = new int[8];
        int blockHash = spread(e.hashCode());
        int counterHash = rehash(blockHash);
        int block = (blockHash & blockMask) << 3;
        for (int i = 0; i < 4; i++) {
            int h = counterHash >>> (i << 3);
            //计算得出table数组中long的对应区区域，一个long有16个区，所以 & 15就可以得出对应的区域
            index[i] = (h >>> 1) & 15;
            int offset = h & 1;
            //计算得出table数组对应的下标，block的最大值与table长度差了8，刚好和后续的offset和(i << 1)的最大值对上
            index[i + 4] = block + offset + (i << 1);
        }
        boolean added =
                incrementAt(index[4], index[0])
                        | incrementAt(index[5], index[1])
                        | incrementAt(index[6], index[2])
                        | incrementAt(index[7], index[3]);

        if (added && (++size == sampleSize)) {
            reset();
        }
    }

    /**
     * 扰动函数
     */
    static int spread(int x) {
        x ^= x >>> 17;
        x *= 0xed5ad4bb;
        x ^= x >>> 11;
        x *= 0xac4c1b51;
        x ^= x >>> 15;
        return x;
    }

    /**
     * 扰动函数
     */
    static int rehash(int x) {
        x *= 0x31848bab;
        x ^= x >>> 14;
        return x;
    }

    /**
     * 如果指定计数器尚未达到最大值 (15)，则将其加 1。
     * <p>
     * i – 表索引，table的下标
     * j – 要递增的计数器
     * @return 是否增加（如果到15上限值就会返回false）
     */
    boolean incrementAt(int i, int j) {
        //<<2相当于 乘以4，j表示long中的区域，范围在0-15，左移（乘以4）后范围在0-60，表示的就是一个long中对应的区域起始位置
        int offset = j << 2;
        //将 1111 左移对应的偏移量作为掩码
        long mask = (0xfL << offset);
        //如果没到15的上限就加1
        if ((table[i] & mask) != mask) {
            table[i] += (1L << offset);
            return true;
        }
        return false;
    }

    /**
     * 保鲜机制，将每个计数器减少其原始值的一半
     */
    void reset() {
        int count = 0;
        for (int i = 0; i < table.length; i++) {
            count += Long.bitCount(table[i] & ONE_MASK);
            // 一个Long记录了16个key的频率，Long除以2以后& RESET_MASK（16个十六进制的7组成）保证了每个key的频率不超过7
            table[i] = (table[i] >>> 1) & RESET_MASK;
        }
        size = (size - (count >>> 2)) >>> 1;
    }

    //获取大于等于给定值的最小二次幂
    static int ceilingPowerOfTwo(int x) {
        return 1 << -Integer.numberOfLeadingZeros(x - 1);
    }
}

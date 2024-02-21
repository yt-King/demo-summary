package com.ytking;


import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import static com.ytking.TairWeb.leaderBoard;

/**
 * @author yt
 * @package: com.ytking
 * @className: dataInit
 * @date 2024/2/20
 * @description: 数据初始化
 */
@Slf4j
public class DataProcesser {
    public static void init() {
        long start = System.currentTimeMillis();
        for (int i = 0; i <= 50000; i++) {
            if (i % 5000 == 0) {
                log.info(i + " data finish");
            }
            leaderBoard.addMember("member" + i, (int) (Math.random() * 10000), (int) (Math.random() * 10000), (int) (Math.random() * 10000));
        }
        long end = System.currentTimeMillis();
        log.info("dataInit timespan: " + (end - start) / 1000.0);
    }
}

package com.ytking;

import com.aliyun.tair.tairzset.LeaderBoard;
import com.aliyun.tair.tairzset.LeaderData;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ytKing
 */
@Slf4j
public class TairWeb {
    // init timeout
    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
    // api timeout
    private static final int DEFAULT_SO_TIMEOUT = 2000;
    private static final int BOARD_SIZE = 100000;
    private static final String HOST = "10.0.1.33";
    private static final int PORT = 31410;
    private static final String PASSWORD = null;
    private static JedisPool jedisPool;
    static LeaderBoard leaderBoard;
    private static final JedisPoolConfig config = new JedisPoolConfig();

    static {
        // JedisPool config: https://help.aliyun.com/document_detail/98726.html
        config.setMaxTotal(32);
        config.setMaxIdle(20);
        jedisPool = new JedisPool(config, HOST, PORT, DEFAULT_CONNECTION_TIMEOUT,
                DEFAULT_SO_TIMEOUT, PASSWORD, 0, null);
        //reverse: true 从大到小排序 ; false 从小到大排序
        //useZeroIndexForRank: true 从0开始排序 ; false 从1开始排序
        leaderBoard = new LeaderBoard("rank", jedisPool, 100, true, false);
    }

    public static void main(String[] args) throws IOException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        // 创建一个定时任务
        Runnable task = TairWeb::cleanBoard;
        // 定时任务在延迟1秒后开始执行，然后每隔3秒执行一次
        scheduler.scheduleAtFixedRate(task, 1, 5, TimeUnit.SECONDS);


        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/add", new SimpleHttpServer.AddHandler());
        server.createContext("/query", new SimpleHttpServer.GetHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server is listening on port 8080");
    }

    public static void cleanBoard() {
        Long pages = leaderBoard.totalPages();
        if (pages > 1000) {
            List<LeaderData> leaders = leaderBoard.leaders(1001);
            Long res = leaderBoard.removeMembersInScoreRange("0#0", leaders.get(0).getScore());
            log.info(res + " removed");
        } else {
            log.info("none removed");
        }
    }
}

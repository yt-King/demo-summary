package com.ytking;

import com.aliyun.tair.tairzset.DistributedLeaderBoard;
import com.aliyun.tair.tairzset.LeaderBoard;
import com.aliyun.tair.tairzset.LeaderData;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

import static com.ytking.TairWeb.leaderBoard;

/**
 * @author yt
 * @package: com.ytking
 * @className: UseDemo
 * @date 2024/2/21
 * @description: UseDemo
 */
@Slf4j
public class UseDemo {
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
    static DistributedLeaderBoard distributedLeaderBoard;
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

    public static void main(String[] args) {
        DataProcesser.init();
        demo();
    }

    public static void demo() {
        addBoard();
        getBoard();
        getBoardPage();
        getBoardInfo();
        getRank();
        removeMember();
        delBoard();
        getBoard();
    }

    public static void addBoard() {
        leaderBoard.addMember("member_a", 1, 2, 3);
        leaderBoard.addMember("member_b", 1, 3, 3);
        leaderBoard.addMember("member_c", 2, 2, 3);
        leaderBoard.addMember("member_d", 4, 4, 4);
        leaderBoard.addMember("member_e", 6, 6, 6);
        leaderBoard.addMember("member_f", 3, 1, 4);
        leaderBoard.addMember("member_g", 9, 0, 1);
        //update 覆盖式更新
        leaderBoard.addMember("member_a", 7, 2, 3);
        //update 增长式更新
        leaderBoard.incrScoreFor("member_b", "1#1#1");
        //remove
        leaderBoard.removeMember("member_e");
    }

    public static void getBoard() {
//        List<LeaderData> leaderData = leaderBoard.allLeaders();
//        log.info("allLeaderData:");
//        leaderData.forEach(System.out::println);
        //指定排名范围，索引从0开始
        List<LeaderData> leaderData1 = leaderBoard.retrieveMember(1, 3);
        log.info("2-4LeaderData:");
        leaderData1.forEach(System.out::println);
        List<LeaderData> top3 = leaderBoard.top(3);
        log.info("top3LeaderData:");
        top3.forEach(System.out::println);
    }

    public static void getBoardPage() {
        List<LeaderData> leaderDataList = leaderBoard.leaders(2);
        log.info("leaderData:");
        leaderDataList.forEach(System.out::println);
    }

    public static void getBoardInfo() {
        log.info("leaderDataPage:" + leaderBoard.totalPages());
        log.info("leaderDataTotalMembers:" + leaderBoard.totalMembers());
    }

    public static void getRank() {
        Long memberG1 = leaderBoard.rankFor("member_g");
        log.info("memberGsRank:" + memberG1);
        String memberG2 = leaderBoard.scoreFor("member_g");
        log.info("memberGsScore:" + memberG2);
        LeaderData memberG3 = leaderBoard.scoreAndRankFor("member_g");
        log.info("memberGsScoreAndRankFor:" + memberG3);
    }

    public static void removeMember() {
        leaderBoard.removeMembersInScoreRange("4#0#0", "6#6#6");
    }

    public static void delBoard() {
        Long delLeaderBoard = leaderBoard.delLeaderBoard();
        log.info("delLeaderBoard:" + delLeaderBoard);
    }
}

package com.ytking;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.*;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_FORMAT;

/**
 * @author yt
 * @package: com.ytking
 * @className: SignUtil
 * @date 2023/8/7
 * @description: 设所有发送或者接收到的数据为集合M，将集合M内的参数按照参数名ASCII码从小到大排序（字典序），
 * 使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA。
 * ◆ 按参数名字母（ASCII码）从小到大排序（字典序）；
 * ◆ 参数名区分大小写；
 * ◆ 系统参数和业务参数全部参与签名
 * ◆ 服务端验证签名时，传送的sign参数不参与签名，将生成的签名与该sign值作校验
 * 在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5加密，
 * 再将得到的字符串所有字符转换为大写，得到sign值signValue
 */
@Slf4j
public class SignUtil {
    public static void main(String[] args) {
        long time = System.currentTimeMillis() / 1000;
        long newTime = System.currentTimeMillis();
        System.out.println(time + "  " + newTime);
        String appSecret = "d1579e0992064bdaba18739235625cce";
        Map<String, Object> signParam = Map.of(
                "uid", "1713130"
        );
        Map<String, Object> res = createSignByMiddle(signParam, appSecret);
        log.info("res:{}", res);
        Map<String, Object> params = Map.of(
                "uid", "1713130",
                "activeId", "64db335a4200050001d16b1e",
                "requestTimestamp", newTime,
                "rewardType", 8
        );
        Map<String, Object> signByMiddle = createSignByMiddle(params, appSecret);
        log.info("signByMiddle:{}", signByMiddle);
        doPost("https://sandbox.platform.moxigame.cn/k8s-pre/istio/grpc-gate/receptionist/isv/v1/checkReward", new JSONObject(signByMiddle));
        Map<String, Object> params1 = Map.of(
                "uid", 1713130,
                "key_id", "xcx64d056f30dab4",
                "timestamp", time,
                "key_secret", "083D694C1089F8C0D8803D7096076577"
        );
        Map<String, Object> sign1 = createSign(params1);
        log.info("sign:{}", sign1);
        Map<String, Object> params2 = Map.of(
                "uid", 1713130,
                "key_id", "xcx64d056f30dab4",
                "timestamp", time,
                "key_secret", "083D694C1089F8C0D8803D7096076577",
                "create_time", DateUtil.format(new Date(), NORM_DATETIME_FORMAT),
                "activity_type", 9,
                "belong_type", 1,
                "reward_type", 7,
                "protein", -5,
                "reward_log_id", "12dasg31dgv35yh12q3g1"
        );
        Map<String, Object> sign2 = createSign(params2);
        log.info("sign:{}", sign2);
    }

    /**
     * MD5加密(中台)
     *
     * @param map
     * @return
     */
    private static Map<String, Object> createSignByMiddle(Map<String, Object> map, String secret) {
        String sign = "";
        HashMap<String, Object> result = new HashMap<>(map);
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            infoIds.sort(Map.Entry.comparingByKey());
            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                String key = item.getKey();
                String val = String.valueOf(item.getValue());
                if (!(Objects.equals(val, "") || val == null)) {
                    sb.append(key).append("=").append(val).append("&");
                }
            }
            String msg = sb.substring(0, sb.length() - 1) + secret;
            log.info("================ascii===============" + msg);
            //MD5加密
            sign = getMD5Str(msg);
            result.put("sign", sign);
            log.info("================signMD5加密===============" + sign);
        } catch (Exception e) {
            log.error("加密失败：{}", (Object) e.getStackTrace());
        }
        return result;
    }

    /**
     * MD5加密
     *
     * @param map
     * @return
     */
    private static Map<String, Object> createSign(Map<String, Object> map) {
        String sign = "";
        HashMap<String, Object> result = new HashMap<>(map);
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            infoIds.sort(Map.Entry.comparingByKey());
            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                String key = item.getKey();
                String val = String.valueOf(item.getValue());
                if (!(Objects.equals(val, "") || val == null)) {
                    sb.append(key).append("=").append(val).append("&");
                }
            }
            String msg = sb.substring(0, sb.length() - 1);
            log.info("================ascii===============" + msg);
            //MD5加密，toUpperCase()：大小写转换
            sign = getMD5Str(msg).toUpperCase();
            result.put("sign", sign);
            result.remove("key_secret");
            log.info("================signMD5加密===============" + sign);
        } catch (Exception e) {
            log.error("加密失败：{}", (Object) e.getStackTrace());
        }
        return result;
    }

    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        assert digest != null;
        return new BigInteger(1, digest).toString(16);
    }

    public static void doPost(String url, JSONObject json) {
        final java.net.http.HttpClient clientSimple = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toJSONString()))
                .build();

        HttpResponse<String> send = null;
        try {
            send = clientSimple.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("send message:{}", send.body());
    }
}

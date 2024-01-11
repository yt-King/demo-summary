package com.ytking.sign.middle;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
                "uid", "1712659"
        );
        Map<String, Object> res = createSignByMiddle(signParam, appSecret);
        log.info("res:{}", res);
        Map<String, Object> params = Map.of(
                "uid", "1713130",
                "activeId", "64db335a4200050001d16b1e",
                "requestTimestamp", newTime,
                "rewardType", 5,
                "sId", "c21f72FcO2La79G3D2Y85YD9"
        );
        Map<String, Object> signByMiddle = createSignByMiddle(params, appSecret);
        log.info("signByMiddle:{}", signByMiddle);
//        doPost("https://sandbox.platform.moxigame.cn/k8s-pre/istio/grpc-gate/receptionist/isv/v1/checkReward", new JSONObject(signByMiddle));
        Map<String, Object> params1 = Map.of(
                "uid", 1713130,
                "key_id", "xcx64d056f30dab4",
                "timestamp", time,
                "key_secret", "083D694C1089F8C0D8803D7096076577"
        );
        Map<String, Object> sign1 = createSign(params1);
        log.info("sign:{}", sign1);
        Map<String, Object> params2 = new HashMap<>(Map.of(
                "uid", 1713130,
                "key_id", "xcx64d056f30dab4",
                "timestamp", time,
                "key_secret", "083D694C1089F8C0D8803D7096076577",
                "create_time", DateUtil.format(new Date(), NORM_DATETIME_FORMAT),
                "activity_type", 9,
                "belong_type", 1,
                "reward_type", 3,
                "reward_log_id", "51e62cM5Z213R54f6eV9BXFD"
        ));
//        params2.put("protein", 10);
//        params2.put("prize_id", 9152);
        params2.put("lng", 120.2);
        params2.put("lat", 30.3);
        params2.put("coupon_id", 8927);
        params2.put("coupon_activity_id", 1494);
        Map<String, Object> sign2 = createSign(params2);
        log.info("sign:{}", sign2);
//        doPost("http://bs.test.pailifan.com/xcx/open/send_game_reward", new JSONObject(sign2));
        //长虹测试
        String url1 = "gw/applet/hongkeCoupon/receiptHongkeCouponForActivity";
        String url2 = "gw/sys/hongkeCoupon/couponDataListPage";
        String url3 = "gw/point/api/v1/integral/trade?method=grant";
        String url4 = "gw/applet/api/appletForeign/queryIsSubWxOffice?unionId=ojeWR0rlY2N3VqGYP2fxLfe7BIog";
        Map<String, Object> map1 = Map.of("couponId", 181, "type", 4, "unionId", "ojeWR0rlY2N3VqGYP2fxLfe7BIog");
        Map<String, Object> map2 = Map.of("useLocation", 3, "pageNum", 1, "pageSize", 10);
        Map<String, Object> map3 = Map.of(
                "phone", "18380438391",
                "appId", "78c05e83d8f84d93aaae2ad41d48181d1697685425058",
                "remarks", "积分发放",
                "code", "102",
                "integral", 1
        );
        doPostOrGetByChanghong(url4, new JSONObject(), "get");
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
     * MD5加密(长虹)
     * 生成SHA1签名，该签名为不可逆签名，仅用于判断是否是正确的数据来源
     *
     * @param secret    接入方密钥
     * @param timestamp 时间戳
     * @param nonce     随机字母
     * @param encrypt   加密数据(appName的base64)
     * @return 签名字符串
     */
    public static String createSignByChanghong(String secret, String timestamp, String nonce, String encrypt) {
        // 先做字符排序
        String[] array = new String[]{secret, timestamp, nonce, encrypt};
        StringBuilder sb = new StringBuilder();
        // 字符串排序
        Arrays.sort(array);
        for (int i = 0; i < 4; i++) {
            sb.append(array[i]);
        }
        String str = sb.toString();
        // sha1 一次
        byte[] dis = DigestUtils.sha1(str.getBytes());
        // sha1 hex 一次
        return DigestUtils.sha1Hex(dis);
    }

    public static void doPostOrGetByChanghong(String url, JSONObject json, String type) {
        String timestamp = System.currentTimeMillis() + "";
        final java.net.http.HttpClient clientSimple = HttpClient.newHttpClient();
        String nonce = "93";
        //接入方名称
        String appName = "摩西";
        //接入方key
        String appCode = "810";
        //接入方密钥
        String secret = "81dad2548f2849ba8a5c455860fb09bb";
        String encrypt = Base64.getEncoder().encodeToString(appName.getBytes(StandardCharsets.UTF_8));
        String signature = createSignByChanghong(secret, timestamp, nonce, encrypt);
        url = "https://devhongke.changhong.com/" + url + (url.contains("?") ? "&" : "?") + "encrypt="
                + encrypt + "&tokenCode=" + appCode + "&nonce=" + nonce + "&timestamp=" + timestamp + "&signature=" + signature;
        System.out.println("url:" + url);
        System.out.println("params:" + json.toJSONString());
        HttpRequest.Builder header = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");
        HttpRequest request = "get".equals(type) ? header.GET().build() : header.POST(HttpRequest.BodyPublishers.ofString(json.toJSONString())).build();
        HttpResponse<String> send;
        try {
            send = clientSimple.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("send message:{}", JSON.parse(send.body()));
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
                if ((Objects.equals(key, "lng")) || (Objects.equals(key, "lat"))) {
                    continue;
                }
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
        String re = null;
        byte encrypt[];
        try {
            byte[] tem = str.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(tem);
            encrypt = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte t : encrypt) {
                String s = Integer.toHexString(t & 0xFF);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                sb.append(s);
            }
            re = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re;
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
        log.info("send message:{}", JSON.parse(send.body()));
    }
}

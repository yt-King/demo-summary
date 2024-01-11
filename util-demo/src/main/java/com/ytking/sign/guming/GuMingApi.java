package com.ytking.sign.guming;


import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GuMingApi {
    static String address = "http://oapi.test1.iguming.net/gapi/newton-open/newton/open";
    static String aesKey = "fhbbbf8a0fkkhnx7";
    static String appKey = "test_moxi";
    static String appSecret = "b962045bd4308fd3dd1074fc";
    static String aesUserId = "b407b165b213b4595f90959aa7d7c2ee177ac952251891b98244b1de1077ea69";

    public static void main(String[] args) {
        String asd = AesUtils.encrypt("asd", aesKey);
        String res = AesUtils.decrypt("Lok6ie1InVKR7fTtnQcfUwRzhhBWxwTC55s1di0f7xEP7GzJjD0yDtn3IPc0I2pPsNFI9ne6AoLOfH2c4V7g912NtDw0b7slmrBKe1e9qcUF9TnaoQkuc8e8WH9oK8ym", aesKey);
        log.info(res);
//        login(aesUserId);
//        sendBenefit();
    }

    public static void login(String code) {
        JSONObject res = post("/moxi/member/info", Map.of("aesUserId", aesUserId));
//        String uid = AesUtils.decrypt(code, aesKey);
//        log.info(res.toJSONString());
    }

    public static void sendBenefit() {
        JSONObject res = null;
//        res = post("/moxi/packet/cover", Map.of("aesUserId", aesUserId));
        String uniqueId = aesUserId + "hbfm" + System.currentTimeMillis() / 100000;
        res = post("/coupon/receive/v2", Map.of("aesUserId", aesUserId, "key", "??", "orderId", uniqueId, "codeType", "USERID"));
        log.info(res.toJSONString());
    }

//    public boolean queryUserIsFavorFans(UserContext userContext) throws Exception {
//        return post("/moxi/follow/account", Map.of("aesUserId", userContext.getUserExt().getAnonymousId())).getBoolean("followAccount");
//    }

    private static JSONObject post(String uri, Map<String, Object> params) {
        String time = String.valueOf(System.currentTimeMillis());
        UrlBuilder url = UrlBuilder.of(address + uri);
        HttpRequest client = new HttpRequest(url);
        client.method(Method.POST);
        client.header("Content-Type", "application/json");
        // 生成签名
        String data = AesUtils.encrypt(JSONUtil.toJsonStr(params), aesKey);
        String sign = SignUtils.getSign(appKey, appSecret, time, data);
        String param = JSON.toJSONString(Map.of("sign", sign, "appKey", appKey, "timestamp", time, "data", data));
        client.body(param);
        String body = client.execute().body();
        log.info("请求url：" + url);
        log.info("参数：" + param);
        log.info("请求结果：" + body);
        return JSON.parseObject(body);
    }
}

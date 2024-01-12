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
    static String aesUserId = "d0c0a6c4c96a2e6d960d44fb9183fde908caf42b263a21c7be1e32707076ca63";

    public static void main(String[] args) {
        login();
        sendPacket();
        sendBenefit();
    }

    public static void login() {
        String res = post("/moxi/member/info", Map.of("aesUserId", aesUserId));
        String decrypt = AesUtils.decrypt(res, aesKey);
        log.info(decrypt);
    }

    public static void sendPacket() {
        String res = post("/moxi/packet/cover", Map.of("aesUserId", aesUserId));
        String decrypt = AesUtils.decrypt(res, aesKey);
        log.info(decrypt);
    }

    public static void sendBenefit() {
        String uniqueId = "yhq1" + System.currentTimeMillis() / 100000;
        String res = post("/coupon/receive/v2", Map.of("aesUserId", aesUserId, "key", "1745281116294488065", "orderId", uniqueId, "codeType", "USERID"));
        String decrypt = AesUtils.decrypt(JSON.parseObject(res).getString("data"), aesKey);
        log.info(decrypt);
    }

    private static String post(String uri, Map<String, Object> params) {
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
        log.info("aesKey：" + aesKey);
        log.info("加密参数：" + JSONUtil.toJsonStr(params));
        log.info("请求参数：" + param);
        log.info("请求结果：" + body);
        JSONObject res = JSON.parseObject(client.execute().body());
        if (res.getInteger("code") != 0) {
            log.error("接口调用失败：{}", res);
            throw new RuntimeException("接口调用失败");
        }
        return res.getString("data");
    }
}

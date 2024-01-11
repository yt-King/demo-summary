package com.ytking.sign.guming;
import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;

/**
 * @author yt
 * @package: cn.moxi.channel.service.infrastructure.apis.guming
 * @className: SignUtils
 * @date 2024/1/4
 * @description: TODO
 */
public class SignUtils {

    /**
     * 验签
     * @return
     */
    public static boolean verify(String appKey, String appSecret, String timestamp, String data, String originSign){
        String sign = getSign(appKey, appSecret, timestamp, data);
        return originSign.equals(sign);
    }

    public static String getSign(String appKey, String appSecret, String timestamp, String data){
        String signStr = appKey + appSecret + timestamp + data;
        return md5DigestAsHex(signStr);
    }

    public static String md5DigestAsHex(String signString) {
        return DigestUtils.md5DigestAsHex(signString.getBytes(Charset.forName("UTF-8")));
    }

}
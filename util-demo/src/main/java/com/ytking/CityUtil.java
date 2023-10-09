package com.ytking;

import net.ipip.ipdb.City;

import java.util.Arrays;
import java.util.Objects;

public class CityUtil {
    private static City city_DB;

    /**
     * 通过IP查询城市
     *
     * @param ip       (IPv4或者 IPv6)
     * @param language
     * @return 例如[中国,广东,广州]
     */
    public static String[] find(String ip, String language) {
        try {
            if (null == city_DB) {
                city_DB = new City(Objects.requireNonNull(CityUtil.class.getResource("/")).getPath() + "ipip.ipdb");
            }
            return city_DB.find(ip, language);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(CityUtil.find("222.74.97.226", "CN")));
    }

}



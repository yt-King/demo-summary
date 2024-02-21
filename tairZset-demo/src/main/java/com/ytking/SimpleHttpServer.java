package com.ytking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tair.tairzset.LeaderData;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.ytking.TairWeb.leaderBoard;

/**
 * @author yt
 * @package: com.ytking
 * @className: SimpleHttpServer
 * @date 2024/2/20
 * @description: SimpleHttpServer
 */
public class SimpleHttpServer {

    static class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Read the request body
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }
            JSONObject jsonObject = JSON.parseObject(String.valueOf(requestBody));
            //时间早的要在前面，所以做个减法
            leaderBoard.addMember(jsonObject.getString("name"), jsonObject.getInteger("score"), (2000000000000.0 - System.currentTimeMillis()));

            // Respond with a simple message
            String response = "success";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<LeaderData> list = leaderBoard.top(200);

            // Respond with a simple message
            String response = JSONArray.toJSONString(list);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

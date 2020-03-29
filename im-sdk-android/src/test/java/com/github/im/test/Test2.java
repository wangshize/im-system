package com.github.im.test;

import com.github.im.sdk.ImClient;

/**
 * @author wangsz
 * @create 2020-03-28
 **/
public class Test2 {

    public static void main(String[] args) throws Exception {
        ImClient client = new ImClient();
        client.connect("127.0.0.1", 8080);
        String userId= "test002";
        client.authenticate(userId, "test_token");
        client.send(userId, userId + " 测试消息");

        while (true) {
            Thread.sleep(1000);
        }
    }
}

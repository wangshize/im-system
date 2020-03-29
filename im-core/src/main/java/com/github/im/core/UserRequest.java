package com.github.im.core;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

/**
 * @author wangsz
 * @create 2020-03-28
 **/
@Data
public class UserRequest {
    private RequestCommand command;
    private String userId;
    private String token;
    private String message;

    public ByteBuf toByteBuf() {
        String json = JSON.toJSONString(this);
        json = json + "#";
        return Unpooled.wrappedBuffer(json.getBytes());
    }
}

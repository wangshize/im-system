package com.github.im.core;

import com.github.im.core.ImConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 请求构建类
 * 封装请求协议字段设置
 * @author wangsz
 * @create 2020-08-12
 **/
public class ProtocolByteBufBuilder {

    public static ByteBuf buildByteBuf(byte[] body, int requestType) {
        int bodyLength = body.length;
        int msgLength = ImConstants.MSG_HEAD_LENGTH + bodyLength;
        ByteBuf requestBuffer = Unpooled.buffer(ImConstants.MSG_HEAD_LENGTH + bodyLength);
        //消息总长度
        requestBuffer.writeInt(msgLength);
        //MSG_HEAD_LENGTH
        requestBuffer.writeInt(ImConstants.MSG_HEAD_LENGTH);
        //CLIENT_SDK_VERSION
        requestBuffer.writeInt(ImConstants.CLIENT_SDK_VERSION);
        //请求类型
        requestBuffer.writeInt(requestType);
        //请求顺序
        requestBuffer.writeInt(ImConstants.SEQUENCE_DEFAULT);
        //消息体长度
        requestBuffer.writeInt(bodyLength);
        //消息体
        requestBuffer.writeBytes(body);
        return requestBuffer;
    }

}

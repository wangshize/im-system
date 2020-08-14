package com.github.im.core;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @author wangsz
 * @create 2020-08-12
 **/
@Data
public class DataPackage {

    private int headerLength;

    private int clientVersion;

    private int requestType;

    private int sequence;

    private int bodyLength;

    private byte[] body;

    public DataPackage(ByteBuf requestBuf) {
        this.headerLength = requestBuf.readInt();
        this.clientVersion = requestBuf.readInt();
        this.requestType = requestBuf.readInt();
        this.sequence = requestBuf.readInt();
        this.bodyLength = requestBuf.readInt();
        this.body = new byte[bodyLength];
        requestBuf.readBytes(body);
    }

}

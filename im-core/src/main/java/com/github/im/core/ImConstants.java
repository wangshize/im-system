package com.github.im.core;

/**
 * @author wangsz
 * @create 2020-08-12
 **/
public class ImConstants {

    /**
     * 最长消息接收长度
     */
    public static final int MAX_FRAME_LENGTH = 64 * 1024 * 1024;

    /**
     * 消息头长度
     */
    public static final int MSG_HEAD_LENGTH = 20;

    /**
     * 消息版本号
     */
    public static final int CLIENT_SDK_VERSION = 1;

    /**
     * 消息顺序
     */
    public static final int SEQUENCE_DEFAULT = 1;

    /**
     * 响应状态：正常
     */
    public static final int RESPONSE_STATUS_OK = 1;
    /**
     * 响应状态：异常
     */
    public static final int RESPONSE_STATUS_ERROR = 2;
    /**
     * 响应异常状态码：未知
     */
    public static final int RESPONSE_ERROR_CODE_UNKNOWN = -1;
    /**
     * 响应异常状态码：认证失败
     */
    public static final int RESPONSE_ERROR_CODE_AUTHENTICATE_FAILURE = 1;
    /**
     * 响应异常状态码：认证时异常
     */
    public static final int RESPONSE_ERROR_CODE_AUTHENTICATE_EXCEPTION = 2;
    /**
     * 响应异常信息
     */
    public static final String RESPONSE_ERROR_MESSAGE_EMPTY = "";


}

package com.github.im.dispatcher;

import com.github.im.core.ImConstants;
import com.github.im.protocol.AuthenticateRequest;
import com.github.im.protocol.AuthenticateResponse;

/**
 * @author wangsz
 * @create 2020-08-12
 **/
public class RequestHandler {

    private RequestHandler() {
    }

    static class Singleton {

        static RequestHandler instance = new RequestHandler();

    }

    public static RequestHandler getInstance() {
        return Singleton.instance;
    }

    /**
     * 处理认证请求
     *
     * @param request
     */
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        String uid = request.getUid();
        String token = request.getToken();
        AuthenticateResponse.Builder builder = AuthenticateResponse.newBuilder();
        builder.setUid(uid);
        builder.setToken(token);
        builder.setTimestamp(System.currentTimeMillis());
        try {
            // 请求SSO单点登录系统，把uid和token发送过去，判断是否是登录的用户
            if(authenticateBySSO(uid, token)) {
                builder.setStatus(ImConstants.RESPONSE_STATUS_OK);
                builder.setErrorCode(ImConstants.RESPONSE_ERROR_CODE_UNKNOWN);
                builder.setErrorMessage(ImConstants.RESPONSE_ERROR_MESSAGE_EMPTY);
            } else {
                builder.setStatus(ImConstants.RESPONSE_STATUS_ERROR);
                builder.setErrorCode(ImConstants.RESPONSE_ERROR_CODE_AUTHENTICATE_FAILURE);
                builder.setErrorMessage(ImConstants.RESPONSE_ERROR_MESSAGE_EMPTY);
            }
        } catch(Exception e) {
            // 请求单点登录系统失败
            e.printStackTrace();
            builder.setStatus(ImConstants.RESPONSE_STATUS_ERROR);
            builder.setErrorCode(ImConstants.RESPONSE_ERROR_CODE_AUTHENTICATE_EXCEPTION);
            builder.setErrorMessage(e.toString());
        }

        AuthenticateResponse response = builder.build();

        System.out.println("已经向SSO单点登录系统认证完毕......");

        return response;
    }

    /**
     * 通过单点登录系统进行用户token的认证
     * @param uid
     * @param token
     * @return
     */
    private Boolean authenticateBySSO(String uid, String token) {
        System.out.println(String.format("单点登陆认证成功，uid = %s, token = %s", uid, token));
        return true;
    }

}

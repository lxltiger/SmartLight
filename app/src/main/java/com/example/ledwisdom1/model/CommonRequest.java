package com.example.ledwisdom1.model;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CommonRequest {
    private static final String TAG = "CommonRequest";
    private static final MediaType MEDIATYPE = MediaType.parse("application/json; charset=utf-8");

    public String password;

    public String password1;
    public String password2;

    public int loginType;

    public String account;

    public String verifyCode;
    //提交反馈
    public String content;
    //    联系方式
    public String phone;

    private CommonRequest(String password, int loginType, String account) {
        this.password = password;
        this.loginType = loginType;
        this.account = account;
    }

    private CommonRequest(String account) {
        this.account = account;
    }

    private CommonRequest(String account, String verifyCode) {
        this.account = account;
        this.verifyCode = verifyCode;
    }

    public CommonRequest(String password1, String password2, String account) {
        this.password1 = password1;
        this.password2 = password2;
        this.account = account;
    }

    public CommonRequest(int loginType, String account, String content, String phone) {
        this.loginType = loginType;
        this.account = account;
        this.content = content;
        this.phone = phone;
    }

    public static RequestBody createLogin(String password, int loginType, String account) {
        CommonRequest commonRequest = new CommonRequest(password, loginType, account);
        String json = new Gson().toJson(commonRequest);

        return RequestBody.create(MEDIATYPE, json);

    }

    //    获取验证码
    public static RequestBody createAuthCode(String account) {
        CommonRequest commonRequest = new CommonRequest(account);
        String json = new Gson().toJson(commonRequest);
        return RequestBody.create(MEDIATYPE, json);

    }

    //    确认验证码
    public static RequestBody createVertifyAuthCode(String account, String authCode) {
        CommonRequest commonRequest = new CommonRequest(account, authCode);
        String json = new Gson().toJson(commonRequest);
        return RequestBody.create(MEDIATYPE, json);
    }
    //    验证账号
    public static RequestBody checkAccount(String account) {
        CommonRequest commonRequest = new CommonRequest(account);
        String json = new Gson().toJson(commonRequest);
        return RequestBody.create(MEDIATYPE, json);

    }

}

package com.example.ledwisdom1.model;

import android.arch.persistence.room.Ignore;

public class RequestResult {


    /**
     * resultCode : 0000
     * resultMsg : 发送成功
     */
    @Ignore
    public  String resultCode;
    @Ignore
    public  String resultMsg;


    public boolean succeed() {
        return "0000".equals(resultCode);
    }
}

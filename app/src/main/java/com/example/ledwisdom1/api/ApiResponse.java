package com.example.ledwisdom1.api;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 对请求结果的封装，同时处理成功返回和失败返回的数据
 * @param <T>
 */
public class ApiResponse<T> {


    public final  int code;
    public final  String errorMsg;
    public final  T body;


    public ApiResponse(Throwable throwable) {
        code=500;
        errorMsg = throwable.getMessage();
        body=null;
    }

    public ApiResponse(Response<T> response) {
        code=response.code();
        if (response.isSuccessful()) {
            errorMsg = "";
            body=response.body();
        }else{
            String msg=null;
            ResponseBody responseBody = response.errorBody();
            if (responseBody != null) {
                try {
                    msg=responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (msg == null || msg.trim().length() == 0) {
                msg = response.message();
            }
            errorMsg = msg;
            body=null;
        }
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

}

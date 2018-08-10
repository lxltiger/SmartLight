package com.example.ledwisdom1.utils;

import android.widget.Toast;

import com.example.ledwisdom1.app.SmartLightApp;

public class ToastUtil {

    private static Toast toast;
    public static void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(SmartLightApp.INSTANCE(), msg, Toast.LENGTH_SHORT);
        }else{
            toast.setText(msg);
        }
        toast.show();
//        Toast.makeText(SmartLightApp.INSTANCE(), msg, Toast.LENGTH_SHORT).show();
    }
}

package com.example.ledwisdom1.utils;

import android.widget.Toast;

import com.example.ledwisdom1.app.SmartLightApp;

public class ToastUtil {
    public static void showToast(String msg) {
        Toast.makeText(SmartLightApp.INSTANCE(), msg, Toast.LENGTH_SHORT).show();
    }
}

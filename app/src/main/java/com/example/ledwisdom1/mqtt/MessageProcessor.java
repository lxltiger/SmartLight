package com.example.ledwisdom1.mqtt;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * author:
 * 时间:2017/12/20
 * qq:1220289215
 * 类描述：处理后台发来的信息
 *
 */

public class MessageProcessor {
    private static final String TAG = MessageProcessor.class.getSimpleName();
    private  Gson mGson;
    private MessageHandler mHandler;
    private HandlerThread mHandlerThread;

    public MessageProcessor() {
        mHandlerThread = new HandlerThread(MessageProcessor.class.getSimpleName(), Process.THREAD_PRIORITY_DEFAULT);
        mHandlerThread.start();
        mHandler = new MessageHandler(mHandlerThread.getLooper(), this);
        mGson = new Gson();
        //数据存储
    }

    public void stop() {
        Log.d(TAG, "stop: ");
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
            mHandlerThread = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mGson=null;

    }

    public void handleMsg(String receiver) {
        mHandler.obtainMessage(0, receiver).sendToTarget();
    }


    void handleConnect(long delay) {
        mHandler.sendEmptyMessageDelayed(1, delay);
    }

    private static final class MessageHandler extends Handler {
        private WeakReference<MessageProcessor> mReference;

        public MessageHandler(Looper looper, MessageProcessor messageProcessor) {
            super(looper);
            mReference = new WeakReference<>(messageProcessor);
        }

        @Override
        public void handleMessage(Message msg) {
            MessageProcessor messageProcessor = mReference.get();
            if (messageProcessor == null) {
                return;
            }
            //连接服务器
            try {
                if (msg.what == 1) {
                    MQTTClient.INSTANCE().connect();
                } else {
                    String receive = (String) msg.obj;
                    Log.d(TAG, receive);

                }
            } catch (MqttException|RuntimeException e) {
                e.printStackTrace();
                Log.e(TAG, "MQTT 连接失败");
                // TODO: 2018/6/20 0020 exit app
            }
        }
    }


    @WorkerThread
    private void process(String action, String receive, JSONObject jsonObject) {

    }




}

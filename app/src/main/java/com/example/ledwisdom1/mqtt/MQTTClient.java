package com.example.ledwisdom1.mqtt;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.utils.ObjectUtils;
import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.HashSet;


/**
 * author:
 * 时间:2017/6/29
 * qq:1220289215
 * 类描述：mqtt通讯客户端
 * 1.需要设置用户名和密码
 * <p>通信是全局的，设置成单例模式
 * <p>
 * <p>
 * 统一信息处理方式，在HandlerThread 的子线程处理
 * MQTTClient 主要处理与MQTT的通信 上下线  订阅 退出等
 */

public class MQTTClient {
    private static final String TAG = MqttClient.class.getSimpleName();
    private Gson mGson;
    private static final String USER_NAME = "test";
    private static final String PASSWORD = "123456";

    private static final String HOST = "tcp://121.40.76.221:1883";

//    private static final String TOPIC ="/ser2dev/529/gatewayId";
//    private static final String TOPIC_ADD="/ser2dev/529/gatewayId";

    private static final String kTopic = "/App2dev/529/";//APP 控制灯
    private static final String DTopic = "/dev2sta/529/";//监听灯的状态
    private static final String STopic = "/dev2ser/529/";//监听网关状态
    //    private static final String STopic ="/dev2sta/529/";
    private static final String GTopic = "/ser2dev/529/";


    private MqttAndroidClient mqttAndroidClient;
    /**
     * 服务送达消息的质量，0～2，2表示肯定送达
     */
    private static final int mQos = 2;
    /**
     * 消息是否被服务器保留
     */
    private static final boolean mRetained = true;
    /**
     * 订阅的主题,用来取消订阅
     */
    private HashSet<String> mTopicSubscribed;
    private volatile static MQTTClient sMQTTClient;
    /**
     * 处理Mqtt接受到的消息 使用BroadCast通知相关页面处理
     */
    private MessageProcessor mMessageProcessor;

    private MQTTClient() {
        mMessageProcessor = new MessageProcessor();
        mTopicSubscribed = new HashSet<>();
        mGson = new Gson();

    }

    public static MQTTClient INSTANCE() {
        if (sMQTTClient == null) {
            synchronized (MQTTClient.class) {
                if (sMQTTClient == null) {
                    sMQTTClient = new MQTTClient();
                }
            }
        }
        return sMQTTClient;
    }

    /**
     * 与服务端连接，建立通信
     * 需要在子线程执行 所以借用带有thread的MessageProcessor
     * 最终连接还是在connect方法中
     */
    public void startConnect() throws IOException {
        Log.d(TAG, "startConnect()");
        mqttAndroidClient = new MqttAndroidClient(SmartLightApp.INSTANCE(), HOST, ObjectUtils.getMacAddress());
        mqttAndroidClient.setCallback(mCallbackExtended);
        mMessageProcessor.handleConnect(0);

    }

//    连接的最终实现

    /**
     * @throws MqttException
     * @throws RuntimeException OPPO和VIVO的手机在低电量的时候启动Sevice会出现user 0 is restricted，所以需要抛出RuntimeException
     *                          https://stackoverflow.com/questions/38764497/security-exception-unable-to-start-service-user-0-is-restricted
     */
    @WorkerThread
    void connect() throws MqttException, RuntimeException {
        Log.d(TAG, "try to connect");
        if (!mqttAndroidClient.isConnected()) {
            mqttAndroidClient.connect(getMqttConnectOptions(), null, mConnectListener);
        } else {
            Log.e(TAG, "startConnect: already connected");
        }

    }


    /**
     * 获取MQTT连接选项
     * 在此设置遗嘱信息
     *
     * @return
     */
    private MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(false);
        mqttConnectOptions.setConnectionTimeout(10);
        mqttConnectOptions.setKeepAliveInterval(20);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(USER_NAME);
        mqttConnectOptions.setPassword(PASSWORD.toCharArray());
        //断开连接的主题
//        String topic = String.format(Locale.CHINA, "/shineecall/%s/disconnect", mParameter.getMac());
        //断开的消息
//        String payLoad = getDisconnectNotification(mParameter.getHost(), mParameter.getMac());
//        mqttConnectOptions.setWill(topic, Base64.encode(payLoad.getBytes(), Base64.DEFAULT), mQos, mRetained);
        return mqttConnectOptions;
    }


    private DisconnectedBufferOptions getBufferOpt() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        return disconnectedBufferOptions;
    }

    /**
     * 连接服务器监听
     */
    private IMqttActionListener mConnectListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.d(TAG, "onSuccess() called with " + mqttAndroidClient.isConnected());
//            dismissDialog();
            onConnectSucceed();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.d(TAG, "Failed to connect to: " + mqttAndroidClient.isConnected());
            //在断交换机重连时会出现重连成功后又调用此回调，导致断网对话框再次显示
            if (mqttAndroidClient.isConnected()) {
                return;
            }
//            showDialog();
            //最终调用本类的connect（）方法
            mMessageProcessor.handleConnect(8000);
        }
    };

    /**
     * MQTT的回调
     */
    private MqttCallbackExtended mCallbackExtended = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
//            dismissDialog();
            if (reconnect) {
                Log.d(TAG, "Reconnected to : " + serverURI);
                onConnectSucceed();
            } else {
                Log.d(TAG, "Connected to : " + serverURI);
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.d(TAG, "The Connection was lost. " + mqttAndroidClient.isConnected());
//            showDialog();
            mMessageProcessor.handleConnect(0);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
//            final String receive = new String(Base64.decode(message.getPayload(), Base64.DEFAULT));
            String receive = new String(message.getPayload());
            Log.d(TAG, "from topic:" + topic + "  Incoming message: " + receive);
            mMessageProcessor.handleMsg(receive);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };


    /**
     * 订阅主题
     *
     * @param subscriptionTopic 所需要订阅的主题，字符串形式
     * @param needPublish       是否需要发布消息 只有在订阅门口屏信息成功时才为true
     */
    private void subscribeToTopic(final String subscriptionTopic, final boolean needPublish) {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, mQos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "subscribeToTopic:" + subscriptionTopic);
                    /*if (needPublish) {
                        //获取门口屏信息
                        Message message = new Message("getdoorscreeninfo", mParameter.getMac(), mParameter.getHost());
                        String getDoorScreenInfo = mGson.toJson(message);
                        String publishTopic = getpublishTopic();
                        publishMessage(getDoorScreenInfo, publishTopic);
                    }*/
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to subscribe");
                }
            });

        } catch (MqttException ex) {
            Log.e(TAG, "Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    /**
     * @param publishMessage 要发布的信息
     * @param publishTopic   要发布的主题
     */
    void publishMessage(String publishMessage, String publishTopic) {
        try {
            mqttAndroidClient.publish(publishTopic, publishMessage.getBytes(), mQos, mRetained);
            Log.d(TAG, "publishMessage " + publishMessage + "\npublishTopic " + publishTopic);
           /* if (!mqttAndroidClient.isConnected()) {
                Log.d(TAG, mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }*/
        } catch (MqttException e) {
            Log.d(TAG, "Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // TODO: 2018/7/16 0016 从default mesh 获取 gateway id
    public void publishLampControlMessage(String hubId, String message) {
        String topic = String.format("%s%s", kTopic, hubId);
        publishMessage(message, topic);
    }


    /**
     * 与服务器连接成功的处理
     */
    void onConnectSucceed() {
        Log.d(TAG, "Connect onSuccess");
        mqttAndroidClient.setBufferOpts(getBufferOpt());
        subscirbe();
    }


    /**
     * 订阅需要的数据：门口屏的，床头屏的等等
     * 在获取到准确的房间号，或断线重连使用
     */
    private void subscirbe() {
        subscribeToTopic(kTopic, false);
        mTopicSubscribed.add(kTopic);
        subscribeToTopic(DTopic, false);
        mTopicSubscribed.add(DTopic);
        subscribeToTopic(STopic, false);
        mTopicSubscribed.add(STopic);
        subscribeToTopic(GTopic, false);
        mTopicSubscribed.add(GTopic);
    }


    /**
     * 取消订阅
     *
     * @throws MqttException
     */
    private void unSubscribe() throws MqttException {
        if (mTopicSubscribed.size() > 0) {
            String[] topics = new String[mTopicSubscribed.size()];
            mTopicSubscribed.toArray(topics);
            mTopicSubscribed.clear();
            mqttAndroidClient.unsubscribe(topics);
        }
    }


    private void exitMqtt() {
        //不为空，已连接
        if (mqttAndroidClient != null) {
            try {
                mqttAndroidClient.setCallback(null);
//                String publishMessage = getDisconnectNotification(mParameter.getHost(), mParameter.getMac());
//                String publishTopic = String.format(Locale.CHINA, "/shineecall/%s/disconnect", mParameter.getMac());
                //断开之前发布通知
//                publishMessage(publishMessage, publishTopic);
                //取消之前的订阅
                unSubscribe();
//                mqttAndroidClient.disconnect();
                mqttAndroidClient.close();
                mqttAndroidClient.unregisterResources();
                mqttAndroidClient = null;
            } catch (MqttException e) {
                Log.d(TAG, "exit exception:" + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 退出程序时调用
     */
    public void exit() {
        Log.d(TAG, "exit: ");
        exitMqtt();
        mMessageProcessor.stop();
        mMessageProcessor = null;
//        释放此单例
        sMQTTClient = null;

    }

}

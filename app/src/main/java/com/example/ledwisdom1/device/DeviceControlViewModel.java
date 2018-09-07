package com.example.ledwisdom1.device;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableFloat;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.widget.RadioGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.LightCommandUtils;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.util.EventListener;

import java.util.List;

/**
 * 灯具控制的逻辑实现
 * mesh事件由{@link MeshEventManager}负责监听，在eventListener处理回调
 * 与UI的交互 通过以下几个Observable字段，BindAdapters 的tintIndicator方法处理了颜色动态设置
 * todo 与灯具的交互使用command pattern
 */
public class DeviceControlViewModel extends ViewModel {
    private static final String TAG = DeviceControlViewModel.class.getSimpleName();

    private final int address;
    /**
     * 亮度
     */
    public final ObservableInt brightness = new ObservableInt(80);
    /**
     * 所选取的颜色 默认是红色的
     */
    public final ObservableInt color = new ObservableInt(Color.RED);
    /**
     * 开关状态
     */
    public final ObservableBoolean status = new ObservableBoolean(true);
    /**
     * 取色盘旋转的角度
     */
    public final ObservableFloat degree = new ObservableFloat(0f);

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int brightness = msg.arg1;
            handleLightSetting(brightness);
        }
    };

    public DeviceControlViewModel(int address) {
        this.address = address;
    }


    public void handleSwitch() {
        LightCommandUtils.toggleLamp(address, !status.get());
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_sleep:
                handleLightSetting(20);
                break;
            case R.id.rb_visit:
                handleLightSetting(100);
                break;
            case R.id.rb_read:
                LightCommandUtils.toggleLampWithDelay(address,!status.get());

                break;
            case R.id.rb_conservation:
                handleLightSetting(40);
                break;
        }
    }

    private void handleLightSetting(int brightness) {
        LightCommandUtils.setBrightness(brightness, address);

    }

    /**
     * SeekBar 调节亮度回调
     * 防止用户频繁移动  使用消息队列 只处理最后的设置
     *
     * @param progress
     * @param fromUser
     */
    public void onProgressChanged(/*SeekBar seekBar,*/ int progress, boolean fromUser) {
        if (fromUser) {
            handler.removeMessages(0);
            Message message = handler.obtainMessage(0, progress, -1);
            handler.sendMessageDelayed(message, 100);
        }
    }

    EventListener<String> eventListener= event -> {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
//                onDeviceStatusChanged((DeviceEvent) event);
                break;

        }
    };

    //一次最多返回两个灯的状态，对组控制的状态反馈使用其中一个即可，返回的meshAddress是单灯的
    @WorkerThread
    protected void onOnlineStatusNotify(NotificationEvent event) {
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList
                = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;
        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            Log.d(TAG, meshAddress+ " onOnlineStatusNotify: "+brightness);
            status.set(brightness>0);
            this.brightness.set(brightness);
        }

    }


    public void onColorChanged(int red, int green, int blue, float degree) {
        this.degree.set(degree);
        int rgb = Color.rgb(red, green, blue);
        color.set(rgb);
        int addr = address;
        byte opcode = (byte) 0xE2;
        byte[] params = new byte[]{0x04, (byte) red, (byte) green, (byte) blue};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        int address;

        public Factory(int address) {
            this.address = address;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new DeviceControlViewModel(address);
        }
    }


}

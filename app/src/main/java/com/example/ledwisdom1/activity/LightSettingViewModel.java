package com.example.ledwisdom1.activity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
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
import android.util.Pair;
import android.widget.RadioGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.command.BrightnessCommand;
import com.example.ledwisdom1.command.ColorCommand;
import com.example.ledwisdom1.command.CommandFactory;
import com.example.ledwisdom1.command.OnOffCommand;
import com.example.ledwisdom1.command.SceneCommand;
import com.example.ledwisdom1.command.TelinkCommandFactory;
import com.example.ledwisdom1.command.TelinkSceneCommand;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.model.LightSetting;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.util.EventListener;

import java.util.List;

/**
 * 灯具控制的逻辑实现
 * mesh事件由 MeshEventManager负责监听，在eventListener处理回调
 * 与UI的交互 通过以下几个Observable字段，BindAdapters 的tintIndicator方法处理了颜色动态设置
 * <p>
 * 对设备的控制采用面向接口的方式，后期如果依赖的库有所改变，只需要更改实现方式
 * <p>
 * 组控制界面是不保存设置的状态的，也没有查询其状态，默认是开的 亮度80 色盘没有旋转角度
 */
public class LightSettingViewModel extends ViewModel {
    private static final String TAG = LightSettingViewModel.class.getSimpleName();

    /**
     * 亮度
     */
    public final ObservableInt brightness = new ObservableInt();
    /**
     * 所选取的颜色 默认是红色的
     */
    public final ObservableInt color = new ObservableInt();
    /**
     * 开关状态
     */
    public final ObservableBoolean status = new ObservableBoolean(true);
    /**
     * 取色盘旋转的角度
     */
    public final ObservableFloat degree = new ObservableFloat(0f);

    // 设备参数
    public MutableLiveData<Pair<String,Lamp>> deviceSettingRequest = new MutableLiveData<>();
    // 设置设备参数结果监听
    public final LiveData<Resource<Lamp>> deviceSettingObserver;


    private Lamp lamp;
    private String id;


    private OnOffCommand onOffCommand;
    private BrightnessCommand brightnessCommand;
    private ColorCommand colorCommand;
    private SceneCommand sceneCommand;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            brightnessCommand.setBrightness(msg.arg1);
        }
    };

    public LightSettingViewModel(LightSetting lightSetting, LightSettingRepository repository) {
        initialize(lightSetting);
        deviceSettingObserver = Transformations.switchMap(deviceSettingRequest, repository::createDeviceSetting);

    }


//    这是灯具设置的中心，需要满足不同种类的情形
    private void initialize(LightSetting lightSetting) {
        int brightness = 0;
        int color = Color.RED;
        float degree = 0f;
        int meshAddress = 0;
        switch (lightSetting.kind) {
            case LAMP: {
                Lamp lamp = lightSetting.lamp;
                brightness = lamp.getBrightness();
                meshAddress = lamp.getDevice_id();
                break;
            }
            case GROUP:
                meshAddress = lightSetting.address;
                brightness = 80;
                break;
            case SCENE: {
                lamp = lightSetting.lamp;
                id = lightSetting.id;
                meshAddress = lamp.getDevice_id();
                brightness = lamp.getBrightness();
                if (lamp.getColor() != 0) {
                    color = lamp.getColor();
                    degree = getDegreeByColor(color);
                }
                break;
            }
        }

        this.status.set(brightness > 0);
        this.brightness.set(brightness);
        this.color.set(color);
        this.degree.set(degree);

        CommandFactory commandFactory = new TelinkCommandFactory(meshAddress);
        onOffCommand = commandFactory.onOffCommand();
        brightnessCommand = commandFactory.brightnessCommand();
        colorCommand = commandFactory.colorCommand();
        sceneCommand = new TelinkSceneCommand(lightSetting.address, meshAddress);

    }

    public void handleSwitch() {
        onOffCommand.turnOnOff(!status.get(), 0);
    }

//    从硬件层面添加灯具到情景
    public void addLampToScene() {
        int color = this.color.get();
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int progress = this.brightness.get();
        sceneCommand.handleSceneOperation(SceneCommand.SceneOperation.ADD, (byte) progress, (byte) red, (byte) green, (byte) blue);

    }

    //添加灯具到后台
    public void addLampToRemote() {
        int color = this.color.get();
        int progress = this.brightness.get();
        lamp.setColor(color);
        lamp.setBrightness(progress);
        deviceSettingRequest.setValue(new Pair<>(id,lamp));
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_sleep:
                brightnessCommand.setBrightness(20);
                break;
            case R.id.rb_visit:
                brightnessCommand.setBrightness(100);
                break;
            case R.id.rb_read:
                onOffCommand.turnOnOff(!status.get(), 5000);
                break;
            case R.id.rb_conservation:
                brightnessCommand.setBrightness(40);
                break;
        }
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

    EventListener<String> eventListener = event -> {
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
    private void onOnlineStatusNotify(NotificationEvent event) {
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList
                = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;
        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            Log.d(TAG, meshAddress + " onOnlineStatusNotify: " + brightness);
            status.set(brightness > 0);
            this.brightness.set(brightness);
        }

    }

    public void onColorChanged(int red, int green, int blue, float degree) {
        this.degree.set(degree);
        int rgb = Color.rgb(red, green, blue);
        color.set(rgb);
        colorCommand.setColor((byte) red, (byte) green, (byte) blue);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacksAndMessages(null);
    }


    private float getDegreeByColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        float degree = 0f;
        if (red == 255) {
            if (blue == 0) {
                degree = green * 60 / 255f;
            } else if (green == 0) {
                degree = (255 - blue) * 60 / 255f + 300;
            }
        } else if (green == 255) {
            if (blue == 0) {
                degree = (255 - red) * 60 / 255f + 60;
            } else if (red == 0) {
                degree = blue * 60 / 255f + 120;
            }
        } else if (blue == 255) {
            if (red == 0) {
                degree = (255 - green) * 60 / 255f + 180;
            } else if (green == 0) {
                degree = red * 60 / 255f + 240;
            }
        }

        return degree;
    }


    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private LightSetting lightSetting;
        private LightSettingRepository repository;

        public Factory(LightSetting lightSetting, LightSettingRepository repository) {

            this.lightSetting = lightSetting;
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new LightSettingViewModel(lightSetting, repository);
        }
    }


}

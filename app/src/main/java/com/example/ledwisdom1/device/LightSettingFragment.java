package com.example.ledwisdom1.device;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.databinding.FragmentLightSettingBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.LightCommandUtils;
import com.example.ledwisdom1.view.RGBView;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 灯具的亮度设置
 * 需要兼任蓝牙和网关控制，场景下蓝牙控制
 */
public class LightSettingFragment extends Fragment implements EventListener<String> {
    public static final String TAG = LightSettingFragment.class.getSimpleName();
    private FragmentLightSettingBinding binding;
    private Lamp lamp;
    private VectorDrawableCompat vectorDrawableCompat;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LightCommandUtils.setBrightness(msg.arg1,lamp.getDevice_id());
        }
    };

    public LightSettingFragment() {
    }

    public static LightSettingFragment newInstance(Lamp lamp) {
        Bundle args = new Bundle();
        args.putParcelable("lamp", lamp);
        LightSettingFragment fragment = new LightSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_drop_down_black_24dp, getActivity().getTheme());
        Bundle arguments = getArguments();
        lamp = arguments.getParcelable("lamp");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_light_setting, container, false);
        binding.ivRgb.setOnColorChangedListenner(listener);

        binding.setHandler(this);
        int brightness = lamp.getBrightness();
        binding.setOn(brightness > 0);
        binding.setProgress(brightness);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        smartLightApp.addEventListener(DeviceEvent.STATUS_CHANGED, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SmartLightApp.INSTANCE().removeEventListener(this);
    }


    /**
     * SeekBar 调节亮度回调
     * 防止用户频繁移动  使用消息队列 只处理最后的设置
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mHandler.removeMessages(0);
            Message message = mHandler.obtainMessage(0, progress, -1);
            mHandler.sendMessageDelayed(message, 100);
        }
    }

    /**
     * 底部按钮切换监听 设置SeekBar进度 改亮度
     *
     * @param group
     * @param checkedId
     */
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_sleep:
                LightCommandUtils.setBrightness(20,lamp.getDevice_id());
                break;
            case R.id.rb_visit:
                LightCommandUtils.setBrightness(100,lamp.getDevice_id());
                break;
            case R.id.rb_read:
                //没有使用网关
                LightCommandUtils.toggleLampWithDelay(lamp.getDevice_id(),!binding.getOn());
                break;
            case R.id.rb_conservation:
                LightCommandUtils.setBrightness(40,lamp.getDevice_id());

                break;
        }
    }



    private RGBView.OnColorChangedListener listener = new RGBView.OnColorChangedListener() {
        @Override
        public void onColorChanged(int red, int green, int blue, float degree) {
            binding.view.setRotation(degree);
            int rgb = Color.rgb(red, green, blue);
            tintIndicator(binding.indicator,rgb);
            int addr = lamp.getDevice_id();
            byte opcode = (byte) 0xE2;
            byte[] params = new byte[]{0x04, (byte) red, (byte) green, (byte) blue};
            TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);
        }

    };

    private void tintIndicator(ImageView view, int color) {
        if (vectorDrawableCompat != null) {
            vectorDrawableCompat.setTint(color);
            view.setImageDrawable(vectorDrawableCompat);
        }

    }

    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_switch:
                LightCommandUtils.toggleLamp(lamp.getDevice_id(), !binding.getOn());
                break;
            case R.id.iv_back:
                getActivity().finish();
                break;
        }
    }

    @Override
    public void performed(Event<String> event) {
        Log.d(TAG, "event type" + event.getType());
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
//                onDeviceStatusChanged((DeviceEvent) event);
                break;

        }
    }

    @WorkerThread
    protected void onOnlineStatusNotify(NotificationEvent event) {

        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList
                = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;
        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            if (meshAddress == lamp.getDevice_id()) {
                binding.setOn(brightness > 0);
                binding.setProgress(brightness);
            }
        }


    }
}

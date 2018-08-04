package com.example.ledwisdom1.device;


import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.databinding.FragmentLightSettingBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampCmd;
import com.example.ledwisdom1.mqtt.MQTTClient;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.LightCommandUtils;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * 灯具的亮度设置
 * 需要兼任蓝牙和网关控制，场景下蓝牙控制
 */
public class LightSettingFragment extends Fragment {
    public static final String TAG = LightSettingFragment.class.getSimpleName();
    private FragmentLightSettingBinding binding;
    private Lamp lamp;


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
        Bundle arguments = getArguments();
        lamp = arguments.getParcelable("lamp");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_light_setting, container, false);
        binding.setHandler(this);
        int brightness = lamp.getBrightness();
        binding.setOn(brightness > 0);
        binding.setProgress(brightness);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DeviceViewModel viewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        //当设备状态变更 会在DeviceFragment中更新数据库
        viewModel.observeLamp(lamp.getDevice_id()).observe(this, new Observer<Lamp>() {
            @Override
            public void onChanged(@Nullable Lamp lamp) {
                if (lamp != null) {
                    int brightness = lamp.getBrightness();
                    binding.setOn(brightness > 0);
                    binding.setProgress(brightness);
                }
            }
        });

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

    public void toggleLamp() {
        int dstAddr = lamp.getDevice_id();
        boolean blueTooth = SmartLightApp.INSTANCE().isBlueTooth();
        byte opcode = (byte) 0xD0;
        boolean on = binding.getOn();
        if (on) {
            if (blueTooth) {
                TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr, new byte[]{0x00, 0x00, 0x00});
            } else {
                toggleLampWithHub(dstAddr, false);
            }
        }else{
            if (blueTooth) {
                TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr, new byte[]{0x01, 0x00, 0x00});
            } else {
                toggleLampWithHub(dstAddr, true);
            }
        }

    }


    private void toggleLampWithHub(int meshAddress, boolean on) {
        LampCmd lampCmd = new LampCmd(5, meshAddress, 1, "0", on ? 100 : 0);
        String message = new Gson().toJson(lampCmd);
        MQTTClient.INSTANCE().publishLampControlMessage("1102F483CD9E6123", message);
    }

}

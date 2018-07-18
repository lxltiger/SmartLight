package com.example.ledwisdom1.device;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.databinding.FragmentLightSettingBinding;
import com.example.ledwisdom1.device.entity.LampCmd;
import com.example.ledwisdom1.mqtt.MQTTClient;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.BindingAdapters;
import com.google.gson.Gson;

import static com.example.ledwisdom1.utils.BindingAdapters.LIGHT_CUT;
import static com.example.ledwisdom1.utils.BindingAdapters.LIGHT_OFF;
import static com.example.ledwisdom1.utils.BindingAdapters.LIGHT_ON;

/**
 * A simple {@link Fragment} subclass.
 * 灯具的亮度设置
 * 需要兼任蓝牙和网关控制，场景下蓝牙控制
 */
public class LightSettingFragment extends Fragment {
    private static final String TAG = LightSettingFragment.class.getSimpleName();
    private int mMeshAddress;
    private FragmentLightSettingBinding mBinding;
    /**
     * 灯具开关状态
     */
    public ObservableInt mLightStatus;
    //灯具亮度
    private int mBrightness;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int brightness = msg.arg1;
            Log.d(TAG, "brightness:" + brightness);
            setLight(brightness);
        }
    };

    public LightSettingFragment() {
        // Required empty public constructor
    }

    public static LightSettingFragment newInstance(int meshAddress, int brightness, int status) {
        Bundle args = new Bundle();
        args.putInt("meshAddress", meshAddress);
        args.putInt("brightness", brightness);
        args.putInt("status", status);
        LightSettingFragment fragment = new LightSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMeshAddress = arguments.getInt("meshAddress");
            Log.d(TAG, "mMeshAddress:" + mMeshAddress);

            mBrightness = arguments.getInt("brightness", 100);
            Log.d(TAG, "mBrightness:" + mBrightness);
            int status = arguments.getInt("status", 0);
            Log.d(TAG, "status:" + status);

            mLightStatus = new ObservableInt(status);
            if (status == LIGHT_OFF) {
                mBrightness = 0;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_light_setting, container, false);
        mBinding.setHandler(this);
        mBinding.setOn(LIGHT_ON == mLightStatus.get());
        mBinding.setProgress(mBrightness);
        return mBinding.getRoot();
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
        int brightness = 0;
        int resId = 0;
        switch (checkedId) {
            case R.id.rb_sleep:
                brightness = 40;
                resId = R.string.sleep;
                break;
            case R.id.rb_visit:
                brightness = 100;
                resId = R.string.com_visit;
                break;
            case R.id.rb_read:
//                brightness = 80;

                resId = R.string.readding;
                break;
            case R.id.rb_conservation:
                resId = R.string.conservation;
                brightness = 40;
                break;
        }
        String mode = String.format("%s%s", getString(R.string.now_pattern), getString(resId));
        mBinding.setProgress(brightness);
        setLight(brightness);
    }

    public void handleClick(View view) {
        Log.d(TAG, "handleClick: ");
        switch (view.getId()) {
            case R.id.iv_switch:
                toggleLamp();
                break;
            case R.id.iv_back:
                getActivity().finish();
                break;
        }
    }

    public void toggleLamp() {
        int dstAddr = mMeshAddress;
        boolean blueTooth = SmartLightApp.INSTANCE().isBlueTooth();

        Log.d(TAG, "dstAddr:" + dstAddr);
        byte opcode = (byte) 0xD0;
        switch (mLightStatus.get()) {
            case LIGHT_OFF:
            case LIGHT_CUT:
//                    开灯
                Log.d(TAG, "open");
                if (blueTooth) {
                    TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr, new byte[]{0x01, 0x00, 0x00});
                } else {
                    toggleLampWithHub(dstAddr, true);
                }
                mLightStatus.set(LIGHT_ON);
                mBinding.setOn(true);
                break;
            case BindingAdapters.LIGHT_ON:
                if (blueTooth) {
                    TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr, new byte[]{0x00, 0x00, 0x00});
                } else {
                    toggleLampWithHub(dstAddr, false);
                }
                mLightStatus.set(LIGHT_OFF);
                mBinding.setOn(false);
                break;
        }
    }


    private void toggleLampWithHub(int meshAddress, boolean on) {
        LampCmd lampCmd = new LampCmd(5, meshAddress, 1, "0", on ? 100 : 0);
        String message = new Gson().toJson(lampCmd);
        MQTTClient.INSTANCE().publishLampControlMessage("1102F483CD9E6123", message);
    }

    /**
     * 发送不同亮度值
     *
     * @param progress
     */
    private void setLight(int progress) {
        int addr = mMeshAddress;
        byte opcode;
        byte[] params;
        opcode = (byte) 0xD2;
        params = new byte[]{(byte) progress};
        TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);
    }


}

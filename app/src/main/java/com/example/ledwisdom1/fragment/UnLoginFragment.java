package com.example.ledwisdom1.fragment;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentUnLoginBinding;
import com.example.ledwisdom1.device.AddLampAdapter;
import com.example.ledwisdom1.device.OnHandleNewLightListener;
import com.example.ledwisdom1.model.Light;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.BindingAdapters;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 打开应用 未登录状态的页面
 * 显示当前出厂的设备
 * 此页面的灯具描述是亮度和mesh mac  适配器和添加灯具页面一样
 * fix 如果个别灯已关 这时全关会导致已关的打开
 */
@Deprecated
public class UnLoginFragment extends BaseFragment {
    private static final String TAG = UnLoginFragment.class.getSimpleName();
    private AddLampAdapter mLightAdapter;
    private List<Light> lights ;

    public UnLoginFragment() {
        // Required empty public constructor
    }

    public static UnLoginFragment newInstance() {

        Bundle args = new Bundle();

        UnLoginFragment fragment = new UnLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentUnLoginBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_un_login, container, false);
        lights=new ArrayList<>();
        mLightAdapter = new AddLampAdapter(lights,mHandleNewLightListener);
        binding.setHandler(this);
        binding.rvScannedLights.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvScannedLights.setAdapter(mLightAdapter);

        return binding.getRoot();
    }

    @WorkerThread
    @Override
    protected void onOnlineStatusNotify(NotificationEvent event) {
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList
                = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;

        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            //看看是否已经存在
            Light light = mLightAdapter.getLight(meshAddress);
            if (light == null) {
                light = new Light();
//            用来在集合中查找
                light.meshAddress = meshAddress;
                light.mAddStatus.set(-1);
                lights.add(light);
            }
            // 修改灯状态
            light.mLightStatus.set(notificationInfo.connectStatus.getValue());
            light.mDescription=String.format(Locale.getDefault(),"%d\n%d",meshAddress,brightness);
        }

        mHandler.post(() -> mLightAdapter.notifyDataSetChanged());

    }

    //蓝牙网络断开
    @Override
    protected void onMeshOffline(MeshEvent event) {
        for (Light light : lights) {
            light.mLightStatus.set(BindingAdapters.LIGHT_CUT);
            light.mDescription=String.format(Locale.getDefault(),"%d\n%d",light.meshAddress,0);
        }
        mLightAdapter.notifyDataSetChanged();
    }

    /**
     * 切换灯具的开关
     *
     * @param open
     */
    public void toggle(boolean open) {
        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;
        byte[] params;
        if (open) {
            params = new byte[]{0x01, 0x00, 0x00};

        } else {
            params = new byte[]{0x00, 0x00, 0x00};
        }
        TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
    }



    private OnHandleNewLightListener mHandleNewLightListener=new OnHandleNewLightListener() {
        @Override
        public void onItemClick(Light light) {
            int dstAddr = light.meshAddress;
            byte opcode = (byte) 0xD0;
            //发送命令后会受到状态变更通知
            switch (light.mLightStatus.get()) {
                case BindingAdapters.LIGHT_OFF:
//                    开灯
                    TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,new byte[]{0x01, 0x00, 0x00});
                    break;
                    case BindingAdapters.LIGHT_ON:
                    TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,new byte[]{0x00, 0x00, 0x00});
                    break;
            }
        }

        @Override
        public boolean onItemLongClick(Light light) {
//            已断开连接不处理
            if (light.mLightStatus.get()== BindingAdapters.LIGHT_CUT) {
                return false;
            }
            Intent intent = new Intent();
            intent.putExtra("meshAddress",light.meshAddress);
            intent.putExtra("brightness",light.brightness);
            intent.putExtra("status",light.mLightStatus.get());
            // TODO: 2018/5/31 0031 LightSetting
            return true;
        }

        @Override
        public void onAddClick(Light light) {
//        do nothing
        }
    };

}

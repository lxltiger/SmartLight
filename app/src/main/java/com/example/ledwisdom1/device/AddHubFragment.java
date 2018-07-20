package com.example.ledwisdom1.device;


import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.EspAES;
import com.espressif.iot.esptouch.util.EspNetUtil;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.databinding.FragmentAddHubBinding;
import com.example.ledwisdom1.databinding.LayoutAddHubBinding;
import com.example.ledwisdom1.databinding.LayoutInputSerialNumberBinding;
import com.example.ledwisdom1.databinding.LayoutStartWifiBinding;
import com.example.ledwisdom1.databinding.LayoutWifiSettingBinding;
import com.example.ledwisdom1.device.entity.AddHubRequest;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.model.TitleBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ledwisdom1.example.com.zxinglib.camera.CaptureActivity;

/*
 *未初始化的网关显示红灯，初始化过程是蓝灯，说明正在链接WIFI，完成WIFI绑定是黄灯，hub链接到Wifi之后 按一下上报自己IP MAC等的参数来激活
 *激活的HUb 其status为1
 *
 *Hub 有可能USB接触不良导致初始化失败
 *
 *添加Hub页面 连接hub到WIFI 暂且不支持5G
 *<p>

 */
public class AddHubFragment extends Fragment implements TitleBar.OnTitleClickListener {
    public static final String TAG = AddHubFragment.class.getSimpleName();

    private static final boolean AES_ENABLE = false;
    private static final String AES_SECRET_KEY = "1234567890123456";
    private FragmentAddHubBinding mBinding;
    private TitleBar mTitleBar;
    //执行连接的操作

    private EsptouchAsyncTask4 mTask;

//网关的序列号


    public ObservableField<String> mSerialNumber = new ObservableField<>("1102F483CD9E6123");
    public ObservableField<String> mSSID = new ObservableField<>("");
    public ObservableField<String> mPassword = new ObservableField<>("12345678");
    /*
     *是否能连接Hub和WiFi，默认false获取到Wifi SSID才能连接*/

    public ObservableBoolean mConnectHubEnable = new ObservableBoolean(false);

    public ObservableBoolean isLoading = new ObservableBoolean(false);

    private String mBSSID = "";
    private DeviceViewModel viewModel;

    public static AddHubFragment newInstance() {
        Bundle args = new Bundle();
        AddHubFragment fragment = new AddHubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_hub, container, false);
        mTitleBar = new TitleBar(true, getString(R.string.title_add2hub_text), -1, this);
        mBinding.setTitleBar(mTitleBar);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateViewPager();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);
        subscribeUI(viewModel);
    }

    private void subscribeUI(DeviceViewModel viewModel) {
        viewModel.addHubObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body.succeed()) {
                        getActivity().finish();
//                        mBinding.addHub.setCurrentItem(3);
                    } else {
                        showToast(apiResponse.body.resultMsg);
                    }
                } else {
                    showToast(apiResponse.errorMsg);
                }

                if (mTask != null) {
                    mTask.cancelProgressDialog();
                    mTask = null;
                }
            }
        });


    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void populateViewPager() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        LayoutAddHubBinding addHubBinding = DataBindingUtil.inflate(inflater, R.layout.layout_add_hub, null, false);
        addHubBinding.setHandler(this);
        LayoutInputSerialNumberBinding inputSerialNumberBinding = DataBindingUtil.inflate(inflater, R.layout.layout_input_serial_number, null, false);
        inputSerialNumberBinding.setHandler(this);
        LayoutWifiSettingBinding wifiSettingBinding = DataBindingUtil.inflate(inflater, R.layout.layout_wifi_setting, null, false);
        wifiSettingBinding.setHandler(this);
        LayoutStartWifiBinding startWifiBinding = DataBindingUtil.inflate(inflater, R.layout.layout_start_wifi, null, false);
        startWifiBinding.setHandler(this);

       /* LayoutSelectMeshBinding selectMeshBinding = DataBindingUtil.inflate(inflater, R.layout.layout_select_mesh, null, false);
        selectMeshBinding.setHandler(this);
        selectMeshBinding.meshList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mMeshAdapter = new MeshAdapter(mOnMeshListener);
        selectMeshBinding.meshList.setAdapter(mMeshAdapter);
        selectMeshBinding.meshList.addItemDecoration(new InsetDecoration(getActivity()));

        LayoutSelectLampsForhubBinding selectLampsForhubBinding = DataBindingUtil.inflate(inflater, R.layout.layout_select_lamps_forhub, null, false);
        selectLampsForhubBinding.setHandler(this);
        selectLampsForhubBinding.lamps.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLampAdapter = new LampAdapter(mOnHandleLampListener);
        mLampAdapter.setShowSelectIcon(true);
        selectLampsForhubBinding.lamps.setAdapter(mLampAdapter);*/


        List<View> views = new ArrayList<>();
        views.add(addHubBinding.getRoot());
        views.add(inputSerialNumberBinding.getRoot());
        views.add(wifiSettingBinding.getRoot());
        views.add(startWifiBinding.getRoot());
//        todo remove reduant
//        views.add(selectMeshBinding.getRoot());
//        views.add(selectLampsForhubBinding.getRoot());

        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(views);
        mBinding.addHub.setAdapter(pagerAdapter);


    }

    @Override
    public void onTitleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                handleBackPressed();
                break;

        }

    }

//回退到上一个页面


    public void handleBackPressed() {
        int currentItem = mBinding.addHub.getCurrentItem();
        if (currentItem > 0) {
            mBinding.addHub.setCurrentItem(--currentItem);
        } else {
            getActivity().finish();
        }
    }


    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.scan_qr_code:
                startActivityForResult(new Intent(getActivity(), CaptureActivity.class), 0);
                break;
            case R.id.input_serial_number:
                mBinding.addHub.setCurrentItem(1, true);
                break;
            case R.id.next_for_serial:
                mBinding.addHub.setCurrentItem(2, true);
                break;
//                Wifi设置页面
            case R.id.next_for_wifi_setting:
                connectHubToWifi();
                break;
            case R.id.next_for_start_wifi:
                mBinding.addHub.setCurrentItem(4, true);
                break;
            case R.id.next_for_select_mesh:

                break;
            case R.id.confirm:
                break;
        }
    }


//连接Hub和Wifi


    private void connectHubToWifi() {
        byte[] ssid = ByteUtil.getBytesByString(mSSID.get());
        byte[] password = ByteUtil.getBytesByString(mPassword.get());
        byte[] bssid = EspNetUtil.parseBssid2bytes(mBSSID);
        if (mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask4(this);
        mTask.execute(ssid, bssid, password);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    onWifiChanged(wifiInfo);
                    break;
            }
        }
    };


    private void onWifiChanged(WifiInfo info) {
        if (info == null) {
            if (mTask != null) {
                mTask.cancelEsptouch();
                mTask = null;
                new AlertDialog.Builder(getActivity())
                        .setMessage("Wifi disconnected or changed")
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
            mConnectHubEnable.set(false);
        } else {
            String ssid = info.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            mSSID.set(ssid);
            mBSSID = info.getBSSID();
            mConnectHubEnable.set(true);
        }
    }

//上传Hub信息


    private void upLoadHub() {
        AddHubRequest addHubRequest = new AddHubRequest(mSerialNumber.get(), mSSID.get(), mPassword.get(), "", "", "");
        viewModel.addHubRequest.setValue(addHubRequest);
    }

/*
        *实现WiFI和Hub的连接
     *Hub只能连一个，所以必须是初始化状态，否则会失败，长按至灯闪烁
     *
    without the
    lock,if
    the user
    tap confirm
    and cancel
    quickly enough,
     *
    the bug
    will arise.
    the reason
    is follows:
            *0.
    task is
    starting created, but
    not finished
     *1.
    the task
    is cancel for
    the task
    hasn't been created, it do nothing
            *2.
    task is
    created
     *3.Oops,
    the task
    should be
    cancelled,
    but it
    is running*/


    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], Void, List<IEsptouchResult>> {

        private WeakReference<AddHubFragment> mActivity;
        private final Object mLock = new Object();
        private ProgressDialog mProgressDialog;
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;

        EsptouchAsyncTask4(AddHubFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        void cancelEsptouch() {

            cancel(true);

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (mResultDialog != null) {
                mResultDialog.dismiss();
            }
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
        }

        void cancelProgressDialog() {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            Fragment activity = mActivity.get();
            mProgressDialog = new ProgressDialog(activity.getActivity());
            mProgressDialog.setMessage(activity.getString(R.string.config_esptouch));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            synchronized (mLock) {
                                if (mEsptouchTask != null) {
                                    mEsptouchTask.interrupt();
                                }
                            }
                        }
                    });
            mProgressDialog.show();
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            AddHubFragment activity = mActivity.get();
            int taskResultCount = 1;
            synchronized (mLock) {
                // !!!NOTICE
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
//                byte[] deviceCountData = params[3];
//                taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
                Context context = activity.getActivity().getApplicationContext();
                if (AES_ENABLE) {
                    byte[] secretKey = AES_SECRET_KEY.getBytes();
                    EspAES aes = new EspAES(secretKey);
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, aes, context);
                } else {
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, null, context);
                }
//                mEsptouchTask.setEsptouchListener(activity.myListener);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            AddHubFragment activity = mActivity.get();
            mResultDialog = new AlertDialog.Builder(activity.getActivity())
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
            mResultDialog.setCanceledOnTouchOutside(false);

            if (result == null) {
                mProgressDialog.dismiss();
                mResultDialog.setMessage("连接失败，端口可能被占用");
//                mResultDialog.setMessage("Create Esptouch task failed, the esptouch port could be used by other thread");
                mResultDialog.show();
                return;
            }

            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {

                if (firstResult.isSuc()) {
                    mProgressDialog.setMessage("连接成功 正在上传");
                    activity.upLoadHub();
                } else {
                    mProgressDialog.dismiss();
                    mResultDialog.setMessage("连接失败，设备可能已被连接过\n请重新初始化");
                    mResultDialog.show();
                    activity.mTask = null;

                }

            } else {
                activity.mTask = null;

            }

        }
    }




/*
        *扫描Hub的二维码返回的结果
     *
             *
    @param
    requestCode
     *
    @param
    resultCode
     *
    @param
    data*/


    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");
            Log.d(TAG, result);
            mSerialNumber.set(result);
            mBinding.addHub.setCurrentItem(2);
        }

    }
}

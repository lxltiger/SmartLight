package com.example.ledwisdom1.activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.Status;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.databinding.ActivityLightSettingBinding;
import com.example.ledwisdom1.model.LightSetting;
import com.example.ledwisdom1.utils.BundleConstant;
import com.example.ledwisdom1.utils.MeshEventManager;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;

/**
 * 单个灯具，场景、情景、闹钟的灯光设置页面
 * 本质都是对单灯进行独立设置
 * 灯具的设备类型有 灯：分为普通灯、灯暖、RGB；插座、面板暂未细分
 */
public class LightSettingActivity extends AppCompatActivity {

    private LightSettingViewModel viewModel;
    private ActivityLightSettingBinding binding;

    public static void start(Context context, LightSetting lightSetting) {
        Intent intent = new Intent(context, LightSettingActivity.class);
        intent.putExtra(BundleConstant.LIGHT_SETTING, lightSetting);
        context.startActivity(intent);
    }

    public static void start(Fragment fragment, LightSetting lightSetting) {
        Intent intent = new Intent(fragment.getContext(), LightSettingActivity.class);
        intent.putExtra(BundleConstant.LIGHT_SETTING, lightSetting);
        fragment.startActivityForResult(intent, Activity.RESULT_FIRST_USER);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_light_setting);
        binding.titleBar.toolbar.setNavigationOnClickListener((view) -> finish());
        LightSetting lightSetting = getIntent().getParcelableExtra(BundleConstant.LIGHT_SETTING);
        if (lightSetting != null) {
            //情景和闹钟下显示目录
            if (lightSetting.showMenu()) {
                binding.titleBar.toolbar.inflateMenu(R.menu.ic_comfirm);
                binding.titleBar.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
                binding.setShowMenu(lightSetting.showMenu());
            }
            LightSettingViewModel.Factory factory = new LightSettingViewModel.Factory(lightSetting, new LightSettingRepository());
            viewModel = ViewModelProviders.of(this, factory).get(LightSettingViewModel.class);
            binding.setViewModel(viewModel);
            //使用Life observer监听页面的生命周期 并交给view model处理
            MeshEventManager.bindEventListener(this, viewModel.eventListener, SmartLightApp.INSTANCE());
//        逻辑都在view model
            binding.setViewModel(viewModel);
            binding.ivRgb.setOnColorChangedListenner(viewModel::onColorChanged);
            subscribeUI(viewModel);
        }

    }

    private void subscribeUI(LightSettingViewModel viewModel) {
        viewModel.deviceSettingObserver.observe(this, resource -> {
            binding.setResource(resource);
            if (Status.SUCCESS == resource.status) {
                viewModel.addLampToScene();
                Intent intent = new Intent();
                intent.putExtra(BundleConstant.LAMP, resource.data);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else if (Status.ERROR == resource.status) {
                showToast(resource.message);
            }
        });
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                viewModel.addLampToRemote();
                return true;
        }
        return false;
    }
}

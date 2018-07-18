package com.example.ledwisdom1.user;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.databinding.FragmentSettingBinding;
import com.example.ledwisdom1.databinding.SettingLayoutAccountBinding;
import com.example.ledwisdom1.databinding.SettingLayoutMainBinding;
import com.example.ledwisdom1.databinding.SettingLayoutModifypswBinding;
import com.example.ledwisdom1.databinding.SettingLayoutProfileBinding;
import com.example.ledwisdom1.utils.AutoClearValue;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 设置页面
 */
public class SettingFragment extends Fragment implements CallBack {
    public static final String TAG = SettingFragment.class.getSimpleName();
    private AutoClearValue<FragmentSettingBinding> bindingSetting;
    private AutoClearValue<SettingLayoutAccountBinding> bindingAccount;
    private AutoClearValue<SettingLayoutMainBinding> bindingMain;
    private AutoClearValue<SettingLayoutProfileBinding> bindingProfile;
    private AutoClearValue<SettingLayoutModifypswBinding> bindingModify;
    private UserViewModel viewModel;


    public static SettingFragment newInstance() {
        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSettingBinding fragmentSettingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        SettingLayoutMainBinding settingLayoutMainBinding = DataBindingUtil.inflate(inflater, R.layout.setting_layout_main, container, false);
        SettingLayoutAccountBinding settingLayoutAccountBinding = DataBindingUtil.inflate(inflater, R.layout.setting_layout_account, container, false);
        SettingLayoutProfileBinding settingLayoutProfileBinding = DataBindingUtil.inflate(inflater, R.layout.setting_layout_profile, container, false);
        SettingLayoutModifypswBinding settingLayoutModifypswBinding = DataBindingUtil.inflate(inflater, R.layout.setting_layout_modifypsw, container, false);

        fragmentSettingBinding.setHandler(this);
        settingLayoutAccountBinding.setHandler(this);
        settingLayoutMainBinding.setHandler(this);
        settingLayoutProfileBinding.setHandler(this);
        settingLayoutModifypswBinding.setHandler(this);

        List<View> viewList = new ArrayList<>();
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        viewList.add(settingLayoutMainBinding.getRoot());
        viewList.add(settingLayoutProfileBinding.getRoot());
        viewList.add(settingLayoutAccountBinding.getRoot());
        viewList.add(settingLayoutModifypswBinding.getRoot());
        fragmentSettingBinding.viewPager.setAdapter(pagerAdapter);
        fragmentSettingBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fragmentSettingBinding.setShowTick(position == 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bindingSetting = new AutoClearValue<>(this, fragmentSettingBinding);
        bindingAccount = new AutoClearValue<>(this, settingLayoutAccountBinding);
        bindingMain = new AutoClearValue<>(this, settingLayoutMainBinding);
        bindingProfile = new AutoClearValue<>(this, settingLayoutProfileBinding);
        bindingModify = new AutoClearValue<>(this, settingLayoutModifypswBinding);
        return fragmentSettingBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        bindingAccount.get().setViewModel(viewModel);
        bindingModify.get().setViewModel(viewModel);
        subscribeUI(viewModel);

    }

    private void subscribeUI(UserViewModel viewModel) {
        viewModel.modifyResponseObserver.observe(this, apiResponse -> {
            if (apiResponse.isSuccessful()) {
                if (apiResponse.body.succeed()) {
                    bindingSetting.get().viewPager.setCurrentItem(2);
                } else {
                    showToast(apiResponse.body.resultMsg);
                }
            } else {
                showToast(apiResponse.errorMsg);
            }
        });

        viewModel.profileLiveData.observe(this, profile -> {
            if (profile != null) {
                bindingProfile.get().setProfile(profile);
                bindingAccount.get().setAccount("手机账号：".concat(profile.phone));
            }
        });

        viewModel.logoutResponseObserver.observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(@Nullable Resource<Boolean> apiResponse) {
                if (apiResponse.data) {
                    UserActivity activity = (UserActivity) getActivity();
                    activity.logout();
                }
                showToast(apiResponse.message);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.profile:
                bindingSetting.get().viewPager.setCurrentItem(1);
                break;
            case R.id.account:
                bindingSetting.get().viewPager.setCurrentItem(2);
                break;
            case R.id.modify_psd:
                bindingSetting.get().viewPager.setCurrentItem(3);
                break;
            case R.id.logout:
                viewModel.logout();
                break;
            case R.id.iv_back:
                handleBackPressed();
                break;
            case R.id.confirm_modify:
                viewModel.modifyPassword();
                break;
        }
    }

    /**
     * 处理返回键
     */
    public boolean handleBackPressed() {
        int currentItem = bindingSetting.get().viewPager.getCurrentItem();
        switch (currentItem) {
            case 0:
                getActivity().finish();
                return true;
            case 1:
            case 2:
                bindingSetting.get().viewPager.setCurrentItem(0);
                return true;
            case 3:
                bindingSetting.get().viewPager.setCurrentItem(2);
                return true;
        }
        return false;
    }


}

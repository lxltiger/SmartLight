package com.example.ledwisdom1.user;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.Config;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.common.AutoClearValue;
import com.example.ledwisdom1.databinding.FragmentSettingBinding;
import com.example.ledwisdom1.databinding.SettingLayoutAccountBinding;
import com.example.ledwisdom1.databinding.SettingLayoutMainBinding;
import com.example.ledwisdom1.databinding.SettingLayoutModifypswBinding;
import com.example.ledwisdom1.databinding.SettingLayoutProfileBinding;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;

/**
 * A simple {@link Fragment} subclass.
 * 设置页面
 */
public class SettingFragment extends Fragment implements CallBack, ProduceAvatarFragment.Listener {
    public static final String TAG = SettingFragment.class.getSimpleName();
    private AutoClearValue<FragmentSettingBinding> bindingSetting;
    private AutoClearValue<SettingLayoutAccountBinding> bindingAccount;
    private AutoClearValue<SettingLayoutMainBinding> bindingMain;
    private AutoClearValue<SettingLayoutProfileBinding> bindingProfile;
    private AutoClearValue<SettingLayoutModifypswBinding> bindingModify;
    private UserViewModel viewModel;
    private UserRequest request = new UserRequest();

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
        fragmentSettingBinding.setHandler(this);

        SettingLayoutMainBinding settingLayoutMainBinding = DataBindingUtil.inflate(inflater, R.layout.setting_layout_main, container, false);
        settingLayoutMainBinding.setHandler(this);

        SettingLayoutAccountBinding settingLayoutAccountBinding = DataBindingUtil.inflate(inflater, R.layout.setting_layout_account, container, false);
        settingLayoutAccountBinding.setHandler(this);

        SettingLayoutProfileBinding settingLayoutProfileBinding = DataBindingUtil.inflate(inflater, R.layout.setting_layout_profile, container, false);
        settingLayoutProfileBinding.setHandler(this);

//        settingLayoutProfileBinding.setUserName(request.userName);

        SettingLayoutModifypswBinding settingLayoutModifypswBinding = DataBindingUtil.inflate(inflater, R.layout.setting_layout_modifypsw, container, false);
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

        /*viewModel.profileLiveData.observe(this, profile -> {
            if (profile != null) {
                bindingProfile.get().setProfile(profile);
                bindingAccount.get().setAccount("手机账号：".concat(profile.phone));
            }
        });*/

        viewModel.userInfoObserver.observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    bindingProfile.get().setAvatar(Config.IMG_PREFIX.concat(user.getIcon()));
                    bindingProfile.get().setUserName(user.getAccount());
                    request.userName = user.getAccount();
                } else {
                    bindingProfile.get().setAvatar("");
                }
            }
        });

        viewModel.logoutResponseObserver.observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(@Nullable Resource<Boolean> apiResponse) {
                if (apiResponse.data) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                    /*UserActivity activity = (UserActivity) getActivity();
                    activity.logout();*/
                }
                showToast(apiResponse.message);
            }
        });

        viewModel.userResponseObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    showToast(apiResponse.body.resultMsg);
                    getActivity().finish();
                }else{
                    showToast("设置失败");
                }
            }
        });
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
            case R.id.avatar:
                ProduceAvatarFragment.newInstance().show(getChildFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case R.id.confirm:
                request.userName = bindingProfile.get().getUserName();
                if (TextUtils.isEmpty(request.userName)) {
                    showToast("用户名不能为空");
                    return;
                }
                //头像文件为空并且不是修改
                if (null == request.userIcon&&TextUtils.isEmpty(bindingProfile.get().getAvatar())) {
                    showToast("还没有选择头像");
                    return;
                }
                viewModel.userRequest.setValue(request);
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


    @Override
    public void onItemClicked(File file) {
        Glide.with(this).load(file).into(bindingProfile.get().avatar);
//        bindingProfile.get().setAvatar(file.getAbsolutePath());
        request.userIcon = file;
    }
}

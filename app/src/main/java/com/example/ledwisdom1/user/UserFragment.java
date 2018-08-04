package com.example.ledwisdom1.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.databinding.FragmentUserBinding;
import com.example.ledwisdom1.databinding.LayoutLoginBinding;
import com.example.ledwisdom1.databinding.LayoutPasswordBinding;
import com.example.ledwisdom1.databinding.LayoutRegisterBinding;
import com.example.ledwisdom1.home.HomeActivity;
import com.example.ledwisdom1.model.CommonRequest;
import com.example.ledwisdom1.utils.AutoClearValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;

import static com.example.ledwisdom1.user.UserActivity.NAVIGATE_TO_FORGET_PSW;
import static com.example.ledwisdom1.user.UserActivity.NAVIGATE_TO_LOGIN;
import static com.example.ledwisdom1.user.UserActivity.NAVIGATE_TO_REGISTER;


/**
 * 登陆、注册、忘记密码页面  UI和逻辑相似
 * <p>
 * 点击按钮先验证有效性 手机号码、密码长度、验证码
 * 获取验证码过程：验证手机号是否注册 》 获取验证码 》验证码获取倒计时
 * 点击下一步流程》 请求确定验证码输入是否正确》进入密码输入界面
 */
public class UserFragment extends Fragment {
    public static final String TAG = UserFragment.class.getSimpleName();

    private CountDownTimer mCountDownTimer;
    //    onViewDestroyed时自动置空
    private AutoClearValue<LayoutRegisterBinding> bindingRegister;
    private AutoClearValue<LayoutLoginBinding> bindingLogin;
    private AutoClearValue<LayoutPasswordBinding> bindingPassword;
    private AutoClearValue<FragmentUserBinding> bindingUserFragment;


    private UserViewModel userViewModel;


    public static UserFragment newInstance() {
        Bundle args = new Bundle();
//        args.putInt("type", type);
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
//            type = arguments.getInt("type", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentUserBinding fragmentUserBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);
        LayoutLoginBinding loginBinding = DataBindingUtil.inflate(inflater, R.layout.layout_login, container, false);
        LayoutRegisterBinding registerBinding = DataBindingUtil.inflate(inflater, R.layout.layout_register, container, false);
        LayoutPasswordBinding passwordBinding = DataBindingUtil.inflate(inflater, R.layout.layout_password, container, false);

        List<View> viewList = new ArrayList<>();
        viewList.add(loginBinding.getRoot());
        viewList.add(registerBinding.getRoot());
        viewList.add(passwordBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        fragmentUserBinding.viewPager.setAdapter(pagerAdapter);
        bindingLogin = new AutoClearValue<>(this, loginBinding);
        bindingRegister = new AutoClearValue<>(this, registerBinding);
        bindingPassword = new AutoClearValue<>(this, passwordBinding);
        bindingUserFragment = new AutoClearValue<>(this, fragmentUserBinding);

        return fragmentUserBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.title.set(getString(R.string.la_login));
//        给fragment设置viewModel
        bindingUserFragment.get().setViewmodel(userViewModel);
//        给View设置viewModel
        bindingLogin.get().setViewModel(userViewModel);
        bindingRegister.get().setViewModel(userViewModel);
        bindingPassword.get().setViewModel(userViewModel);

        subscribeUI(userViewModel);


    }


    private void subscribeUI(UserViewModel userViewModel) {
        userViewModel.actionObserver.observe(this, action -> {
            if (action == null) {
                return;
            }
            switch (action) {
                case NAVIGATE_TO_REGISTER:
                    bindingRegister.get().getAuthCode.setText(getResources().getString(R.string.ra_getyzm));
                    bindingRegister.get().getAuthCode.setClickable(true);
                    bindingUserFragment.get().viewPager.setCurrentItem(1, true);
                    break;
                case NAVIGATE_TO_FORGET_PSW:
                    bindingRegister.get().getAuthCode.setText(getResources().getString(R.string.ra_getyzm));
                    bindingRegister.get().getAuthCode.setClickable(true);
                    bindingRegister.get().toLogin.setVisibility(View.INVISIBLE);
                    bindingUserFragment.get().viewPager.setCurrentItem(1, true);
                    break;
                case NAVIGATE_TO_LOGIN:
                    navigateToLogin();
                    break;

            }
        });
        //验证手机号监听
        userViewModel.checkAccountResponseObserver.observe(this, apiResponse -> {
            userViewModel.loading.set(false);
            if (userViewModel.isRegister){
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body.succeed()) {//开始进入注册界面
                        RequestBody requestBody = CommonRequest.createVertifyAuthCode(userViewModel.account.get(),userViewModel.authcode.get());
                        userViewModel.authCodeVertifyRequest.setValue(requestBody);
                    } else {
                        showToast(apiResponse.body.resultMsg);
                    }
                } else {
                    showToast(apiResponse.errorMsg);
                }
            }else {
                if (apiResponse.isSuccessful()) {
                    if (!apiResponse.body.succeed()) {//忘记密码
                        RequestBody requestBody = CommonRequest.createAuthCode(userViewModel.account.get());
                        userViewModel.authCodeRequest.setValue(requestBody);
                    } else {
                        showToast(apiResponse.body.resultMsg);
                    }
                } else {
                    showToast(apiResponse.errorMsg);
                }
            }
        });
//        获取验证码监听
        userViewModel.authCodeResponseObserver.observe(this, apiResponse -> {
            userViewModel.loading.set(false);
            if (apiResponse.isSuccessful()) {
                if (apiResponse.body.succeed()) {
                    countDown();
                    bindingRegister.get().signInButton.setEnabled(true);
                } else {
                    showToast(apiResponse.body.resultMsg);
                }
            } else {
                showToast(apiResponse.errorMsg);
            }
        });
//        验证码校验监听
        userViewModel.authCodeVertifyResponseObserver.observe(this, apiResponse -> {
            userViewModel.loading.set(false);
            if (apiResponse.isSuccessful()) {
                if (apiResponse.body.succeed()) {
                    cancelCountDown();
                    bindingUserFragment.get().viewPager.setCurrentItem(2, true);
                    if (NAVIGATE_TO_FORGET_PSW == userViewModel.actionObserver.getValue()) {
                        userViewModel.title.set(getString(R.string.ra_reset));
                    }
                } else {
                    showToast(apiResponse.body.resultMsg);
                }
            } else {
                showToast(apiResponse.errorMsg);
            }
        });
        //   注册
        userViewModel.registerResponseObserver.observe(this, apiResponse -> {
            userViewModel.loading.set(false);
            if (apiResponse.isSuccessful()) {
                if (apiResponse.body.succeed()) {
                    userViewModel.login();
                } else {
                    showToast(apiResponse.body.resultMsg);
                }
            } else {
                showToast(apiResponse.errorMsg);
            }
        });

        userViewModel.loginResponseObserver.observe(this, loginResponse -> {
            if (loginResponse == null) {
                return;
            }
            userViewModel.loading.set(false);
            Boolean data = loginResponse.data;
            if (data) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
                getActivity().finish();
            }
            showToast(loginResponse.message);
        });

    }

    //进入登陆UI 需要取消倒计时
    private void navigateToLogin() {
        cancelCountDown();
        userViewModel.title.set(getString(R.string.la_login));
        bindingUserFragment.get().viewPager.setCurrentItem(0, true);

    }


    private void countDown() {
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bindingRegister.get().getAuthCode.setClickable(false);
                bindingRegister.get().getAuthCode.setText(String.format(Locale.CHINA, "%d s", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                bindingRegister.get().getAuthCode.setText(getResources().getString(R.string.ra_refresh));
                bindingRegister.get().getAuthCode.setClickable(true);
            }
        };
        mCountDownTimer.start();

    }

    private void cancelCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelCountDown();
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    //对返回键的处理  当前是注册或忘记密码 出来返回键 否则交给activity出来
    public boolean handleBackPressed() {
        Integer currentAction = userViewModel.actionObserver.getValue();
        if (null!=currentAction&&(NAVIGATE_TO_REGISTER == currentAction || NAVIGATE_TO_FORGET_PSW == currentAction)) {
            navigateToLogin();
            return true;
        }
        return false;
    }

}

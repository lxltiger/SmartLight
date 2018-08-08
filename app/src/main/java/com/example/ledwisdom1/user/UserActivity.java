package com.example.ledwisdom1.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.common.NavigatorController;

/**
 * 开机启动页面
 * 登陆、注册等UI
 * 每个功能由fragment实现，activity作为容器，UserViewModel实现了UI间的通讯
 */

public class UserActivity extends AppCompatActivity {
    private static final String TAG = UserActivity.class.getSimpleName();

    private NavigatorController mController;

    public static final String ACTION_LOGIN = "action_login";
    public static final String ACTION_SETTING = "action_setting";
    public static final String ACTION_ABOUT_US = "action_about_us";
    public static final String ACTION_FEED_BACK = "action_feed_back";

    public static final int NAVIGATE_TO_REGISTER = 1;
    public static final int NAVIGATE_TO_LOGIN = 2;
    public static final int NAVIGATE_TO_MAIN = 3;
    public static final int NAVIGATE_TO_FORGET_PSW = 4;

    public static final int EMPTY_ACCOUNT = 50;
    public static final int INVALID_PSW = 51;
    public static final int SAME_PSW = 52;
    public static final int EMPTY_AUTH = 53;
    public static final int INVALID_ACCOUNT = 54;
    public static final int INVALID_ORIGIN_PSW = 55;
    public static final int EMPTY_CONTENT = 56;
    public static final int EMPTY_CONTACT = 57;
    private String action;

    public static void start(Context context, String action) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra("action", action);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        mController = new NavigatorController(this, R.id.fl_container);
        if (savedInstanceState == null) {
            handleNavigate(getIntent());
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNavigate(intent);
    }

    private void handleNavigate(Intent intent) {
//        Intent intent = getIntent();
        if (intent != null) {
            action = intent.getStringExtra("action");
            switch (action) {
                case ACTION_LOGIN:
                    mController.navigateToLogin();
                    break;
                case ACTION_SETTING:
                    mController.navigateToSetting();
                    break;
                case ACTION_ABOUT_US:
                    mController.navigateToAboutUs();
                    break;
                case ACTION_FEED_BACK:
                    mController.navigateToFeedBack();
                    break;
            }
        }
    }



    @Override
    public void onBackPressed() {
//        按返回按钮需要区别处理 当前是注册界活密码修改面需要返回到登陆界面，
        FragmentManager fm = getSupportFragmentManager();
        boolean consume = false;
        switch (action) {
            case ACTION_LOGIN:
                UserFragment userFragment = (UserFragment) fm.findFragmentByTag(UserFragment.TAG);
                if (userFragment != null) {
                    consume = userFragment.handleBackPressed();
                }
                break;
            case ACTION_SETTING:
                SettingFragment settingFragment = (SettingFragment) fm.findFragmentByTag(SettingFragment.TAG);
                if (settingFragment != null) {
                    consume = settingFragment.handleBackPressed();
                }
                break;
        }

        if (!consume) {
            super.onBackPressed();
        }
        /*if (!mController.navigateToLast()) {
            super.onBackPressed();
        }*/

    }
}


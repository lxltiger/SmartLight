package com.example.ledwisdom1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.home.HomeActivity;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.user.Profile;
import com.example.ledwisdom1.user.UserActivity;

/**
 * 启动页
 * 通过数据库个人资料判断跳转页面
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        HomeRepository.INSTANCE(this).profileObserver.observe(this, this::handleNavigate);
//        retrieveUserProfileFromLocal();
    }

    /**
     * 从本地获取用户信息
     */
    private void retrieveUserProfileFromLocal() {

//        SmartLightDataBase db = SmartLightDataBase.INSTANCE(this);
//        Profile profile = db.user().getProfile();


    }

    private void handleNavigate(Profile profile) {
        Intent intent = null;
        if (profile != null) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, UserActivity.class);
            intent.putExtra("action", UserActivity.ACTION_LOGIN);
        }
        startActivity(intent);
        finish();
    }
}

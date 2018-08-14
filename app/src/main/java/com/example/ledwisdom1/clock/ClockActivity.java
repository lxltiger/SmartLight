package com.example.ledwisdom1.clock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.common.NavigatorController;

public class ClockActivity extends AppCompatActivity {
    public static final String ACTION_CLOCK = "action_clock";
    public static final String ACTION_CLOCK_LIST = "action_clock_list";
    public static final String ACTION_LAMP_LIST = "action_lamp_list";

    private NavigatorController navigatorController;

    public static void start(Context context, String action, Clock clock) {
        Intent intent = new Intent(context, ClockActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("clock", clock);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        navigatorController = new NavigatorController(this, R.id.fl_container);
        if (savedInstanceState == null) {
            handleNavigate(getIntent());
        }
    }


    private void handleNavigate(Intent intent) {
        String action = "";
        if (intent != null) {
            action = intent.getStringExtra("action");
            switch (action) {
                case ACTION_CLOCK:
                    Clock clock = intent.getParcelableExtra("clock");
                    //如果是修改闹钟 需要获取已选择的灯具，
                    navigatorController.navigateToClock(clock);
                    break;
                case ACTION_CLOCK_LIST:
                    navigatorController.navigateToClockList();
                    break;
            }
        }
    }

    /*single top 模式 */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNavigate(intent);
    }
}

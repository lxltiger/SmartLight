package com.example.ledwisdom1.clock;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.common.NavigatorController;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;

public class ClockActivity extends AppCompatActivity {
    public static final String ACTION_CLOCK = "action_clock";
    public static final String ACTION_CLOCK_LIST = "action_clock_list";
    public static final String ACTION_LAMP_LIST = "action_lamp_list";
    public static final String ACTION_SELECTED_LAMP = "action_selected_lamp";

    private ClockViewModel viewModel;
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
        viewModel = ViewModelProviders.of(this).get(ClockViewModel.class);
        navigatorController = new NavigatorController(this, R.id.fl_container);
        if (savedInstanceState == null) {
            handleNavigate(getIntent());
        }
    }

    private void subscribeUI(ClockViewModel viewModel) {
        viewModel.clockObserver.observe(this, new Observer<ClockResult>() {
            @Override
            public void onChanged(@Nullable ClockResult clockResult) {
                viewModel.isLoading.set(false);
                if (null != clockResult ) {
                    // TODO: 2018/7/26 0026 闹钟处理
                    onBackPressed();

                } else {
                    viewModel.isLoading.set(false);
                    showToast("创建闹钟失败");
                }
            }
        });

        /*viewModel.updateClockObserver.observe(this, new Observer<RequestResult>() {
            @Override
            public void onChanged(@Nullable RequestResult requestResult) {
                viewModel.isLoading.set(false);
                if (requestResult != null) {
                    // TODO: 2018/7/26 0026 闹钟处理
                    onBackPressed();
                }else{
                    showToast("更新闹钟失败");
                }
            }
        });*/
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
                case ACTION_LAMP_LIST:
//                    navigatorController.navigateToClockLampList();
                    break;
                case ACTION_SELECTED_LAMP:
//                    navigatorController.navigateToClockSelectedLamps();
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
